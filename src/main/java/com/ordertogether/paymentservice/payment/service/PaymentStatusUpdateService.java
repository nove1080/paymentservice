package com.ordertogether.paymentservice.payment.service;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentOrder;
import com.ordertogether.paymentservice.payment.domain.PaymentOrderHistory;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.PaymentStatusUpdateReason;
import com.ordertogether.paymentservice.payment.repository.PaymentRepository;
import com.ordertogether.paymentservice.payment.service.command.PaymentStatusUpdateCommand;
import com.ordertogether.paymentservice.exception.InvalidPaymentStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentStatusUpdateService {

    private final PaymentRepository paymentRepository;

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
     * @param command
     */
    private void updatePaymentStatusToExecuting(PaymentStatusUpdateCommand command) {
        PaymentEvent paymentEvent = paymentRepository.selectPaymentEvent(command.orderId());
        paymentEvent.updatePaymentKey(command.paymentKey());
        paymentEvent.getPaymentOrders()
            .forEach(it -> {
                insertPaymentHistory(it, PaymentStatus.EXECUTING, PaymentStatusUpdateReason.PAYMENT_EXECUTING.getDescription());
                it.changePaymentStatus(PaymentStatus.EXECUTING);
            });
    }

    private void updatePaymentStatusToSuccess(PaymentStatusUpdateCommand command) {
        PaymentEvent paymentEvent = paymentRepository.selectPaymentEvent(command.orderId());
        paymentEvent.getPaymentOrders().forEach(it -> {
            insertPaymentHistory(it, PaymentStatus.SUCCESS, PaymentStatusUpdateReason.PAYMENT_CONFIRMED.getDescription());
            it.changePaymentStatus(PaymentStatus.SUCCESS);
        });

        paymentEvent.done(command.successExtraInfo());
    }

    private void updatePaymentStatusToFail(PaymentStatusUpdateCommand command) {
        PaymentEvent paymentEvent = paymentRepository.selectPaymentEvent(command.orderId());
        paymentEvent.getPaymentOrders().forEach(it -> {
            insertPaymentHistory(it, PaymentStatus.FAIL, command.failureExtraInfo().message());
            it.changePaymentStatus(PaymentStatus.FAIL);
        });
    }

    private void updatePaymentStatusToUnknown(PaymentStatusUpdateCommand command) {
        PaymentEvent paymentEvent = paymentRepository.selectPaymentEvent(command.orderId());
        paymentEvent.getPaymentOrders().forEach(it -> {
            insertPaymentHistory(it, PaymentStatus.UNKNOWN, command.failureExtraInfo().message());
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
