package com.ordertogether.paymentservice.payment.service.result;

import com.ordertogether.paymentservice.payment.domain.PaymentFailure;
import com.ordertogether.paymentservice.payment.domain.PaymentMethod;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.exception.InvalidPaymentStatusException;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;

@Builder
public record PGConfirmResult(
    String paymentKey,
    OrderId orderId,
    PaymentStatus status,
    SuccessExtraInfo successExtraInfo,
    PaymentFailure failureExtraInfo
) {

    public PGConfirmResult {
        Objects.requireNonNull(status, "결제 상태는 null 일 수 없습니다.");
        if (!status.isConfirmResult()) {
            throw new InvalidPaymentStatusException(orderId, status);
        }
    }

    public static PGConfirmResult success(String paymentKey, OrderId orderId, SuccessExtraInfo successExtraInfo) {
        return PGConfirmResult.builder()
            .paymentKey(paymentKey)
            .orderId(orderId)
            .status(PaymentStatus.SUCCESS)
            .successExtraInfo(successExtraInfo)
            .build();
    }

    @Builder
    public record SuccessExtraInfo(
        PaymentMethod paymentMethod,
        Long amount,
        LocalDateTime approvedAt
    ) {}
}
