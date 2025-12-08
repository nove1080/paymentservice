package com.ordertogether.paymentservice.payment.web.request;

public record TossPaymentsConfirmRequest(
    String paymentKey,
    String orderId,
    Long amount
) {

}
