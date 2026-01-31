package com.ordertogether.paymentservice.payment.controller.response;

import com.ordertogether.paymentservice.payment.domain.PaymentFailure;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.service.result.PaymentConfirmResult;
import java.util.Objects;
import lombok.Builder;

@Builder
public record PaymentConfirmResponse(
    PaymentStatus paymentStatus,
    String errorCode,
    String errorMessage
) {
    public PaymentConfirmResponse {
        Objects.requireNonNull(paymentStatus, "paymentStatus 는 null 일 수 없습니다.");
        if (paymentStatus.isNotSuccess() && (errorCode == null || errorMessage == null)) {
            throw new IllegalArgumentException("결제 실패에 따른 사유는 null 일 수 없습니다.");
        }
    }

    public static PaymentConfirmResponse from(PaymentConfirmResult confirmResult) {
        return confirmResult.paymentStatus().isSuccess()
            ? success(confirmResult.paymentStatus())
            : failure(confirmResult.paymentStatus(), confirmResult.failure());
    }

    public static PaymentConfirmResponse success(PaymentStatus status) {
        return PaymentConfirmResponse.builder()
            .paymentStatus(status)
            .build();
    }

    public static PaymentConfirmResponse failure(PaymentStatus status, PaymentFailure failure) {
        return PaymentConfirmResponse.builder()
            .paymentStatus(status)
            .errorCode(failure.code())
            .errorMessage(failure.message())
            .build();
    }
}
