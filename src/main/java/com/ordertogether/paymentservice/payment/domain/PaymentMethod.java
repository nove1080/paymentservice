package com.ordertogether.paymentservice.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    CARD("카드 결제"),
    EASY_PAY("간편 결제"),
    UNKNOWN("알 수 없음");

    private final String description;
}
