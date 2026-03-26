package com.ordertogether.paymentservice.payment.service.command;

import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import lombok.Builder;

@Builder
public record PGConfirmCommand(
    String paymentKey,
    OrderId orderId,
    Long amount
) {

    public static PGConfirmCommand from(PaymentConfirmCommand command) {
        return PGConfirmCommand.builder()
                .paymentKey(command.paymentKey())
                .orderId(command.orderId())
                .amount(command.amount())
            .build();
    }

}
