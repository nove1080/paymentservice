package com.ordertogether.paymentservice.payment.infrastructure.toss.mapper;

import com.ordertogether.paymentservice.payment.domain.PaymentMethod;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TossPaymentMethodMapper {

    public static PaymentMethod mapToPaymentMethod(String tossPaymentMethod) {
        return switch (tossPaymentMethod) {
            case "카드" -> PaymentMethod.CARD;
            case "간편결제" -> PaymentMethod.EASY_PAY;
            default -> PaymentMethod.UNKNOWN;
        };
    }

}
