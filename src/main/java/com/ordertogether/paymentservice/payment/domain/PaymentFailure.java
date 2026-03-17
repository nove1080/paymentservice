package com.ordertogether.paymentservice.payment.domain;

import lombok.Builder;

@Builder
public record PaymentFailure (
    String code,
    String message
) {

    public static PaymentFailure from(Throwable e) {
        return PaymentFailure.builder()
            .code(e.getClass().getName())
            .message(e.getMessage() != null ? e.getMessage() : PaymentStatus.UNKNOWN.getDescription())
            .build();
    }

}
