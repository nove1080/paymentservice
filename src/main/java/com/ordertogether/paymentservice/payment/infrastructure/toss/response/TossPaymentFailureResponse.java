package com.ordertogether.paymentservice.payment.infrastructure.toss.response;

public record TossPaymentFailureResponse(
    String code,
    String message
) {

}
