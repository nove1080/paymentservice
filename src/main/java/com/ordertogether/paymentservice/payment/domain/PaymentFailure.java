package com.ordertogether.paymentservice.payment.domain;

import lombok.Builder;

@Builder
public record PaymentFailure (
    String code,
    String message
) {

}
