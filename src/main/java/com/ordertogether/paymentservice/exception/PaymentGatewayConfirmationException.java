package com.ordertogether.paymentservice.exception;

import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 결제 게이트웨이에서 결제 승인 과정 중에 발생하는 예외
 */
@Getter
public class PaymentGatewayConfirmationException extends PaymentException {

    private final String paymentKey;
    private final OrderId orderId;
    private final PaymentStatus paymentStatus;
    private final String errorCode;
    private final String errorMessage;

    public PaymentGatewayConfirmationException(String paymentKey, OrderId orderId, PaymentStatus paymentStatus, String errorCode,
        String errorMessage) {
        super(errorMessage);
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Boolean isRetryable() {
        return paymentStatus.isUnknown();
    }
}
