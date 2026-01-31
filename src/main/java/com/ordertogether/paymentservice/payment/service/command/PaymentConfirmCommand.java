package com.ordertogether.paymentservice.payment.service.command;

import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import lombok.Builder;

@Builder
public record PaymentConfirmCommand(
    OrderId orderId,
    String paymentKey,
    Long amount
) {

}
