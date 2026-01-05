package com.ordertogether.paymentservice.payment.web.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CheckoutRequest(
    @NotNull Long buyerId,
    @NotEmpty List<Long> productIds,
    @NotNull String seed
) {

}
