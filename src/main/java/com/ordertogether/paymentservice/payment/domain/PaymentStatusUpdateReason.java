package com.ordertogether.paymentservice.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatusUpdateReason {
    PAYMENT_EXECUTING("결제 승인 요청"),
    PAYMENT_CONFIRMED("결제 승인 완료"),
    PAYMENT_RETRY_EXHAUSTED("결제 재시도 횟수 초과"),
    PAYMENT_RECOVERY_EXECUTING("결제 복구 실행"),
    ;

    private final String description;
}
