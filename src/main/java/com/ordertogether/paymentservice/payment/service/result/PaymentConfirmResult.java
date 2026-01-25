package com.ordertogether.paymentservice.payment.service.result;

import com.ordertogether.paymentservice.payment.domain.PaymentFailure;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import java.util.Objects;
import lombok.Builder;

@Builder
public record PaymentConfirmResult(
    PaymentStatus paymentStatus,
    PaymentFailure failure
) {

    public PaymentConfirmResult {
        Objects.requireNonNull(paymentStatus, "paymentStatus 는 null 일 수 없습니다.");
        if (paymentStatus.isNotSuccess()) {
            Objects.requireNonNull(failure, "결제 실패에 따른 사유는 null 일 수 없습니다.");
        }
    }

}
