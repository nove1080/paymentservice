package com.ordertogether.paymentservice.exception;

import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import lombok.Getter;

/**
 * 결제 재시도 횟수 만료에 대한 예외
 */
@Getter
public class PaymentRetryExhaustedException extends RuntimeException {

    private static final String MESSAGE = "결제 재시도 횟수가 초과되었습니다. [paymentKey = %s, orderId = %s, errorCode = %s, errorMessage = %s, paymentStatus = %s, retryCount = %d]";

    private final String paymentKey;
    private final OrderId orderId;
    private final String errorCode;
    private final String errorMessage;
    private final PaymentStatus paymentStatus;
    private final Integer retryCount;
    private final Throwable lastException;

    public PaymentRetryExhaustedException(String paymentKey, OrderId orderId, String errorCode,
        String errorMessage,
        PaymentStatus paymentStatus, Integer retryCount, Throwable lastException) {
        super(MESSAGE.formatted(paymentKey, orderId.value(), errorCode, errorMessage, paymentStatus.name(), retryCount), lastException);
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.paymentStatus = paymentStatus;
        this.retryCount = retryCount;
        this.lastException = lastException;
    }
}
