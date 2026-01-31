package com.ordertogether.paymentservice.payment.service.command;

import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import lombok.Builder;

@Builder
public record PGConfirmCommand(
    String paymentKey,
    OrderId orderId,
    Long amount
) {

}
