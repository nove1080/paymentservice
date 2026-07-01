package com.ordertogether.paymentservice.payment.service;

import com.ordertogether.paymentservice.exception.InvalidPaymentStatusException;
import com.ordertogether.paymentservice.payment.domain.PaymentConfirmMessage;
import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentOrder;
import com.ordertogether.paymentservice.payment.domain.PaymentOrderHistory;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.PaymentStatusUpdateReason;
import com.ordertogether.paymentservice.payment.repository.PaymentRepository;
import com.ordertogether.paymentservice.payment.service.command.PaymentStatusUpdateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentStatusUpdateService {

    private final PaymentOutboxService paymentOutboxService;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 결제 상태 업데이트
     *
     * @param command
     */
    @Transactional
    public void updatePaymentStatus(PaymentStatusUpdateCommand command) {
        switch (command.status()) {
            case EXECUTING -> updatePaymentStatusToExecuting(command);
            case SUCCESS -> updatePaymentStatusToSuccess(command);
            case FAIL -> updatePaymentStatusToFail(command);
            case UNKNOWN -> updatePaymentStatusToUnknown(command);
            default -> throw new InvalidPaymentStatusException(command.orderId(), command.status());
        }
    }

    /**
     * 결제 상태를 EXECUTING 으로 변경
     * - 결제 키(payment key) 업데이트
     * - 결제 주문들의 상태를 EXECUTING 으로 변경
     * @param command 상태 업데이트 커맨드
     */
    private void updatePaymentStatusToExecuting(PaymentStatusUpdateCommand command) {
        PaymentEvent paymentEvent = paymentRepository.selectPaymentEvent(command.orderId());
        paymentEvent.updatePaymentKey(command.paymentKey());
        paymentEvent.getPaymentOrders()
            .forEach(it -> {
                insertPaymentHistory(it, PaymentStatus.EXECUTING, command.reason() != null ? command.reason().getDescription() : PaymentStatusUpdateReason.PAYMENT_EXECUTING.getDescription());
                it.changePaymentStatus(PaymentStatus.EXECUTING);
            });
    }

    /**
     * 결제 상태를 SUCCESS 로 변경
     * - 결제 주문들의 상태를 SUCCESS 로 변경
     * - 결제 완료 이벤트 발행
     * @param command 상태 업데이트 커맨드
     */
    private void updatePaymentStatusToSuccess(PaymentStatusUpdateCommand command) {
        PaymentEvent paymentEvent = paymentRepository.selectPaymentEvent(command.orderId());
        paymentEvent.getPaymentOrders().forEach(it -> {
            insertPaymentHistory(it, PaymentStatus.SUCCESS, command.reason() != null ? command.reason().getDescription() : PaymentStatusUpdateReason.PAYMENT_CONFIRMED.getDescription());
            it.changePaymentStatus(PaymentStatus.SUCCESS);
        });
        paymentEvent.done(command.successExtraInfo());
        PaymentConfirmMessage message = paymentOutboxService.insertPaymentOutbox(command);
        applicationEventPublisher.publishEvent(message);
    }

    private void updatePaymentStatusToFail(PaymentStatusUpdateCommand command) {
        PaymentEvent paymentEvent = paymentRepository.selectPaymentEvent(command.orderId());
        paymentEvent.increaseFailedCount();
        paymentEvent.getPaymentOrders().forEach(it -> {
            insertPaymentHistory(it, PaymentStatus.FAIL, command.reason() != null ? command.reason().getDescription() : command.failureExtraInfo().message());
            it.changePaymentStatus(PaymentStatus.FAIL);
        });
    }

    private void updatePaymentStatusToUnknown(PaymentStatusUpdateCommand command) {
        PaymentEvent paymentEvent = paymentRepository.selectPaymentEvent(command.orderId());
        paymentEvent.increaseFailedCount();
        paymentEvent.getPaymentOrders().forEach(it -> {
            insertPaymentHistory(it, PaymentStatus.UNKNOWN, command.reason() != null ? command.reason().getDescription() : command.failureExtraInfo().message());
            it.changePaymentStatus(PaymentStatus.UNKNOWN);
        });
    }

    private void insertPaymentHistory(PaymentOrder paymentOrder, PaymentStatus nextStatus, String reason) {
        paymentRepository.insertPaymentHistory(
            PaymentOrderHistory.builder()
                .paymentOrderId(paymentOrder.getId())
                .previousStatus(paymentOrder.getPaymentStatus())
                .currentStatus(nextStatus)
                .reason(reason)
                .build()
        );
    }

}
