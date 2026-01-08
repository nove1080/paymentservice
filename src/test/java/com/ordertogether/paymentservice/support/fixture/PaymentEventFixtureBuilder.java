package com.ordertogether.paymentservice.support.fixture;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentMethod;
import com.ordertogether.paymentservice.payment.domain.PaymentOrder;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentEventFixtureBuilder {

    private Long buyerId = 1L;

    private List<PaymentOrder> paymentOrders = new ArrayList<>();

    private String orderName = "";

    private String paymentKey = "TEST-payment-key-001";

    private String orderId = "TEST-order-id-001";

    private boolean isPaymentDone = false;

    private PaymentMethod method = PaymentMethod.EASY_PAY;

    private LocalDateTime approvedAt = LocalDateTime.of(2025, 1, 1, 0, 0);

    public PaymentEventFixtureBuilder addPaymentOrder(PaymentOrder paymentOrder) {
        paymentOrders.add(paymentOrder);
        orderName = paymentOrders.stream()
            .map(PaymentOrder::getProductName)
            .collect(Collectors.joining(", "));
        return this;
    }

    public PaymentEventFixtureBuilder withOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public PaymentEvent build() {
        return PaymentEvent.builder()
            .buyerId(buyerId)
            .paymentOrders(paymentOrders)
            .orderName(orderName)
            .paymentKey(paymentKey)
            .orderId(new OrderId(orderId))
            .isPaymentDone(isPaymentDone)
            .method(method)
            .approvedAt(approvedAt)
            .build();
    }

}
