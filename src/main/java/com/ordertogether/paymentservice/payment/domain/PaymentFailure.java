package com.ordertogether.paymentservice.payment.domain;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public record PaymentFailure (
    String code,
    String message
) {

}
