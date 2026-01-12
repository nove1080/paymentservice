package com.ordertogether.paymentservice.payment.dto.request;

import lombok.Builder;

@Builder
public record TossPaymentsConfirmRequest(
    String paymentKey,
    String orderId,
    Long amount
) {

}
