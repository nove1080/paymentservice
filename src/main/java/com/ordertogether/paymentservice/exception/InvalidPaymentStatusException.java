package com.ordertogether.paymentservice.exception;

import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;

public class InvalidPaymentStatusException extends InvalidPaymentException {

    private static final String INVALID_CONFIRMATION_STATUS_MESSAGE = "잘못된 결제 승인 상태 입니다. [orderId = %s, 결제 상태 = %s]";
    private static final String INVALID_STATUS_WITH_ID_MESSAGE = "잘못된 결제 상태 입니다. [id = %s, 결제 상태 = %s]";

    public InvalidPaymentStatusException(OrderId orderId, PaymentStatus status) {
        super(INVALID_CONFIRMATION_STATUS_MESSAGE.formatted(orderId.value(), status.name()));
    }

    public InvalidPaymentStatusException(Long id, PaymentStatus status) {
        super(INVALID_STATUS_WITH_ID_MESSAGE.formatted(id, status.name()));
    }
}
