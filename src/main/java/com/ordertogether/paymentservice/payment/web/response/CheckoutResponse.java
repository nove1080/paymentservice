package com.ordertogether.paymentservice.payment.web.response;

import com.ordertogether.paymentservice.payment.service.result.CheckoutResult;
import lombok.Builder;

@Builder
public record CheckoutResponse(
    String orderId,
    String orderName,
    Long totalAmount
) {

    public static CheckoutResponse from(CheckoutResult result) {
        return CheckoutResponse.builder()
            .orderId(result.orderId())
            .orderName(result.orderName())
            .totalAmount(result.totalAmount())
            .build();
    }

}
