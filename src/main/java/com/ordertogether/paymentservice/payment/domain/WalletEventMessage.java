package com.ordertogether.paymentservice.payment.domain;

import lombok.Builder;

@Builder
public record WalletEventMessage(
    String type,
    String orderId
) {

    public static class Topic {
        public static final String SETTLEMENT_SUCCESS = "settlement-success";
    }
}
