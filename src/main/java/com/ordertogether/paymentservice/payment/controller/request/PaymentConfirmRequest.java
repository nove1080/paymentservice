package com.ordertogether.paymentservice.payment.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PaymentConfirmRequest(
    @NotNull String paymentKey,
    @NotNull String orderId,
    @NotNull Long amount
) {

}
