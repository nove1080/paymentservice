package com.ordertogether.paymentservice.payment.domain;

import java.util.List;
import lombok.Builder;

@Builder
public record PaymentConfirmMessage(
    Long buyerId,
    String orderId,
    String paymentKey,
    List<SimplePaymentOrder> paymentOrders,
    Long amount
) {

    public static PaymentConfirmMessage from(PaymentEvent paymentEvent) {
        return PaymentConfirmMessage.builder()
            .buyerId(paymentEvent.getBuyerId())
            .orderId(paymentEvent.getOrderId().value())
            .paymentKey(paymentEvent.getPaymentKey())
            .paymentOrders(SimplePaymentOrder.from(paymentEvent))
            .amount(paymentEvent.totalAmount())
            .build();
    }

    public record SimplePaymentOrder(
        Long sellerId,
        Long productId,
        Long amount
    ) {

        public static List<SimplePaymentOrder> from(PaymentEvent event) {
            return event.getPaymentOrders().stream()
                .map(SimplePaymentOrder::from)
                .toList();
        }

        public static SimplePaymentOrder from(PaymentOrder paymentOrder) {
            return new SimplePaymentOrder(
                paymentOrder.getSellerId(),
                paymentOrder.getProductId(),
                paymentOrder.getAmount().value().longValue()
            );
        }
    }

}
