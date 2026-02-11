package com.ordertogether.paymentservice.exception;

import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 결제 게이트웨이에서 결제 승인 과정 중에 발생하는 예외
 */
@Getter
@RequiredArgsConstructor
public class PaymentGatewayConfirmationException extends RuntimeException {

    private final String paymentKey;
    private final OrderId orderId;
    private final PaymentStatus paymentStatus;
    private final String errorCode;
    private final String errorMessage;

    public Boolean isRetryable() {
        return paymentStatus.isUnknown();
    }
}
