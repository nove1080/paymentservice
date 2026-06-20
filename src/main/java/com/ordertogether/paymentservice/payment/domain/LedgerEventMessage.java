package com.ordertogether.paymentservice.payment.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
public record LedgerEventMessage(
    String type,
    String orderId
){

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Topic {
        public static final String LEDGER_RECORD_SUCCESS = "ledger-record-success";
    }

}
