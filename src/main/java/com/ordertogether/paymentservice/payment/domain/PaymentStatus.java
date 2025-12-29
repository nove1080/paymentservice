package com.ordertogether.paymentservice.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    NOT_STARTED("결제 승인 시작 전"),
    EXECUTING("결제 승인 중"),
    SUCCESS("결제 승인 완료"),
    FAIL("결제 승인 실패"),
    UNKNOWN("알 수 없음");

    private final String description;
}
