package com.ordertogether.paymentservice.exception.handler;

import com.ordertogether.paymentservice.exception.InvalidPaymentException;
import com.ordertogether.paymentservice.exception.PaymentGatewayConfirmationException;
import com.ordertogether.paymentservice.payment.domain.PaymentFailure;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.service.PaymentStatusUpdateService;
import com.ordertogether.paymentservice.payment.service.command.PaymentConfirmCommand;
import com.ordertogether.paymentservice.payment.service.command.PaymentStatusUpdateCommand;
import com.ordertogether.paymentservice.payment.service.result.PaymentConfirmResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 결제 승인 과정에서 발생하는 예외를 적절히 처리합니다.
 */
@Component
@RequiredArgsConstructor
public class PaymentConfirmExceptionHandler {

    private final PaymentStatusUpdateService paymentStatusUpdateService;

    @Transactional
    public PaymentConfirmResult handle(PaymentConfirmCommand command, Throwable e) {
        PaymentStatus status = resolveStatus(e);
        PaymentFailure failure = resolveFailure(e);

        updatePaymentStatus(command, status, failure);

        return PaymentConfirmResult.builder()
            .paymentStatus(status)
            .failure(failure)
            .build();
    }
    private PaymentStatus resolveStatus(Throwable e) {
        return switch (e) {
            case InvalidPaymentException ex -> PaymentStatus.FAIL;
            case PaymentGatewayConfirmationException ex -> PaymentStatus.FAIL;
            default -> PaymentStatus.UNKNOWN;
        };
    }

    private PaymentFailure resolveFailure(Throwable e) {
        return switch (e) {
            case InvalidPaymentException ex -> PaymentFailure.builder()
                .code(ex.getClass().getName())
                .message(ex.getMessage())
                .build();
            case PaymentGatewayConfirmationException ex -> PaymentFailure.builder()
                .code(ex.getClass().getName())
                .message(ex.getMessage())
                .build();
            default -> PaymentFailure.builder()
                .code(PaymentStatus.UNKNOWN.name())
                .message(PaymentStatus.UNKNOWN.getDescription())
                .build();
        };
    }

    private void updatePaymentStatus(PaymentConfirmCommand command, PaymentStatus status, PaymentFailure failure) {
        paymentStatusUpdateService.updatePaymentStatus(
            PaymentStatusUpdateCommand.builder()
                .paymentKey(command.paymentKey())
                .orderId(command.orderId())
                .status(status)
                .failureExtraInfo(failure)
                .build()
        );
    }

}
