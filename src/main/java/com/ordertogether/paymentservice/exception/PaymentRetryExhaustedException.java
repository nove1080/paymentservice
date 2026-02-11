package com.ordertogether.paymentservice.exception;

import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 결제 재시도 횟수 만료에 대한 예외
 */
@Getter
@RequiredArgsConstructor
public class PaymentRetryExhaustedException extends RuntimeException {

    private final String paymentKey;
    private final OrderId orderId;
    private final String errorCode;
    private final String errorMessage;
    private final PaymentStatus paymentStatus;
    private final Integer retryCount;
    private final Throwable lastException;

}
