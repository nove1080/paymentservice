package com.ordertogether.paymentservice.exception;

import com.ordertogether.paymentservice.payment.domain.vo.OrderId;

public class InvalidPaymentException extends RuntimeException {

    private static final String AMOUNT_MISMATCH_MESSAGE =
        "결제 금액이 일치하지 않습니다. [주문 식별자: %s, 예상 금액: %d, 실제 결제 금액: %d]";

    public InvalidPaymentException(String message) {
        super(message);
    }

    public InvalidPaymentException(OrderId orderId, Long expectedAmount, Long actualAmount) {
        super(AMOUNT_MISMATCH_MESSAGE.formatted(orderId.value(), expectedAmount, actualAmount));
    }
}
