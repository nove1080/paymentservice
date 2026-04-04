package com.ordertogether.paymentservice.payment.service;

import com.ordertogether.paymentservice.exception.handler.PaymentConfirmExceptionHandler;
import com.ordertogether.paymentservice.payment.config.PaymentRecoveryProperties;
import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.PaymentStatusUpdateReason;
import com.ordertogether.paymentservice.payment.repository.PaymentRepository;
import com.ordertogether.paymentservice.payment.service.command.PGConfirmCommand;
import com.ordertogether.paymentservice.payment.service.command.PaymentConfirmCommand;
import com.ordertogether.paymentservice.payment.service.command.PaymentStatusUpdateCommand;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(
    prefix = "payment.recovery",
    name = "enabled",
    havingValue = "true"
)
@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentRecoveryService {

    private static final int RECOVERY_SCHEDULE_DELAY_SECONDS = 120;

    private final PaymentRecoveryProperties recoveryProperties;
    private final PaymentRepository paymentRepository;
    private final PaymentStatusUpdateService paymentStatusUpdateService;
    private final PaymentValidateService paymentValidateService;
    private final PaymentGatewayClient paymentGatewayClient;
    private final PaymentConfirmExceptionHandler paymentConfirmExceptionHandler;

    @Scheduled(fixedDelay = RECOVERY_SCHEDULE_DELAY_SECONDS, timeUnit = TimeUnit.SECONDS)
    public void recovery() {
        log.info("결제 복구 스케줄 시작");

        List<PaymentEvent> recoverableEvents = paymentRepository.selectRecoverablePaymentEvents(
            recoveryProperties.getMaxRetryCount(),
            recoveryProperties.getMinAgeSeconds()
        );

        log.info("복구 대상 조회 결과 {}건", recoverableEvents.size());

        for (PaymentEvent event : recoverableEvents) {
            PaymentConfirmCommand confirmCommand = PaymentConfirmCommand.builder()
                .paymentKey(event.getPaymentKey())
                .orderId(event.getOrderId())
                .amount(event.totalAmount())
                .build();
            try {
                doRecovery(confirmCommand);
            } catch (Exception e) {
                paymentConfirmExceptionHandler.handle(confirmCommand, e);
            }
        }
    }

    private void doRecovery(PaymentConfirmCommand command) {
        log.info("결제 복구 시작 - orderId={}, paymentKey={}", command.orderId(), command.paymentKey());
        paymentStatusUpdateService.updatePaymentStatus(PaymentStatusUpdateCommand.builder()
            .paymentKey(command.paymentKey())
            .orderId(command.orderId())
            .status(PaymentStatus.EXECUTING)
            .reason(PaymentStatusUpdateReason.PAYMENT_RECOVERY_EXECUTING)
            .build());
        paymentValidateService.validateAmount(command.orderId(), command.amount());
        PGConfirmResult pgConfirmResult = paymentGatewayClient.confirmPayment(PGConfirmCommand.from(command));
        paymentStatusUpdateService.updatePaymentStatus(PaymentStatusUpdateCommand.from(pgConfirmResult));
        log.info("결제 복구 결과 - orderId={}, paymentKey={}, result={}", pgConfirmResult.orderId(), pgConfirmResult.paymentKey(), pgConfirmResult.status());
    }
}
