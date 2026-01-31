package com.ordertogether.paymentservice.exception;

import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;

public class PaymentAlreadyProceedException extends InvalidPaymentException{

    private static final String MESSAGE = "이미 처리된 결제 입니다. [주문 식별자: %s, 결제 상태: %s]";

    private static final String SIMPLE_MESSAGE = "이미 처리된 결제 입니다. [주문 식별자: %s]";

    public PaymentAlreadyProceedException(OrderId orderId, PaymentStatus paymentStatus) {
        super(MESSAGE.formatted(orderId.value(), paymentStatus.name()));
    }

    public PaymentAlreadyProceedException(OrderId orderId) {
        super(SIMPLE_MESSAGE.formatted(orderId.value()));
    }

}
