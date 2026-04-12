package com.ordertogether.paymentservice.payment.service.command;

import com.ordertogether.paymentservice.payment.domain.PaymentFailure;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.PaymentStatusUpdateReason;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult.SuccessExtraInfo;
import java.util.Objects;
import lombok.Builder;

@Builder
public record PaymentStatusUpdateCommand(
    OrderId orderId,
    String paymentKey,
    PaymentStatus status,
    PaymentStatusUpdateReason reason,
    SuccessExtraInfo successExtraInfo,
    PaymentFailure failureExtraInfo
) {
    public PaymentStatusUpdateCommand {
        Objects.requireNonNull(orderId);
        Objects.requireNonNull(status);

        if (status.isExecuting()) {
            Objects.requireNonNull(paymentKey);
        }
        if (status.isSuccess() && successExtraInfo == null) {
            throw new IllegalArgumentException("결제 상태 SUCCESS 시 SuccessExtraInfo 는 필수입니다.");
        }
        if (status.isFail() && failureExtraInfo == null) {
            throw new IllegalArgumentException("결제 상태 FAIL 시 PaymentFailure 는 필수입니다.");
        }
    }

    public static PaymentStatusUpdateCommand from(PGConfirmResult pgConfirmResult) {
        return PaymentStatusUpdateCommand.builder()
                .orderId(pgConfirmResult.orderId())
                .paymentKey(pgConfirmResult.paymentKey())
                .status(pgConfirmResult.status())
                .successExtraInfo(pgConfirmResult.successExtraInfo())
                .failureExtraInfo(pgConfirmResult.failureExtraInfo())
            .build();
    }

    public boolean isNotSuccess() {
        return status.isNotSuccess();
    }
}
