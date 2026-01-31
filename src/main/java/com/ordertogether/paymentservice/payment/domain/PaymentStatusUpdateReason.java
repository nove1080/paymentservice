package com.ordertogether.paymentservice.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatusUpdateReason {
    PAYMENT_EXECUTING("결제 승인 요청"),
    PAYMENT_CONFIRMED("결제 승인 완료");

    private final String description;
}
