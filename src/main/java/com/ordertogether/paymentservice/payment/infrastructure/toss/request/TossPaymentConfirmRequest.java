package com.ordertogether.paymentservice.payment.infrastructure.toss.request;

import lombok.Builder;

@Builder
public record TossPaymentConfirmRequest(
    String paymentKey,
    String orderId,
    Long amount
) {

}
