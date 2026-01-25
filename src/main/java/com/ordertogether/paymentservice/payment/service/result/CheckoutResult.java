package com.ordertogether.paymentservice.payment.service.result;

import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import lombok.Builder;

@Builder
public record CheckoutResult (
    OrderId orderId,
    String orderName,
    Long totalAmount
) {

}
