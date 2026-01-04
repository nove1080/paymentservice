package com.ordertogether.paymentservice.payment.service.command;

import java.util.List;
import lombok.Builder;

@Builder
public record CheckoutCommand (
    Long buyerId,
    List<Long> productIds,
    String idempotencyKey
) {

}
