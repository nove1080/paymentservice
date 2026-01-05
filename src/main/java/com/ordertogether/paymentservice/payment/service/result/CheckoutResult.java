package com.ordertogether.paymentservice.payment.service.result;

import lombok.Builder;

@Builder
public record CheckoutResult (
    String orderId,
    String orderName,
    Long totalAmount
) {

}
