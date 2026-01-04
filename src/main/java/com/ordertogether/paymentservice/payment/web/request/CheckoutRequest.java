package com.ordertogether.paymentservice.payment.web.request;

import java.util.List;

public record CheckoutRequest(
    Long buyerId,
    List<Long> productIds,
    String seed
) {

}
