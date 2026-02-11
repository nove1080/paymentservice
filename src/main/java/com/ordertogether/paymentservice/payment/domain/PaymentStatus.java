package com.ordertogether.paymentservice.payment.domain;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString(exclude = "nextStatuses")
@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    NOT_STARTED("결제 승인 시작 전"),
    EXECUTING("결제 승인 중"),
    SUCCESS("결제 승인 완료"),
    FAIL("결제 승인 실패"),
    UNKNOWN("알 수 없음");

    private final String description;
    private List<PaymentStatus> nextStatuses;

    static {
        NOT_STARTED.nextStatuses = List.of(EXECUTING, UNKNOWN);
        EXECUTING.nextStatuses = List.of(SUCCESS, FAIL, UNKNOWN);
        SUCCESS.nextStatuses = Collections.emptyList();
        FAIL.nextStatuses = Collections.emptyList();
        UNKNOWN.nextStatuses = List.of(EXECUTING);
    }

    public boolean isNotStarted() {
        return this == NOT_STARTED;
    }

    public boolean isExecuting() {
        return this == EXECUTING;
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }

    public boolean isNotSuccess() {
        return !isSuccess();
    }

    public boolean isFail() {
        return this == FAIL;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }

    public boolean isConfirmResult() {
        return this == SUCCESS || this == FAIL || this == UNKNOWN;
    }

    public boolean canTransitionTo(PaymentStatus nextStatus) {
        return nextStatuses.contains(nextStatus);
    }
}
