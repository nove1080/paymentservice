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

    private String paymentKey = "";

    private OrderId orderId = OrderId.valueOf("test-order-id");

    private boolean isPaymentDone = false;

    private PaymentMethod method = PaymentMethod.EASY_PAY;

    private LocalDateTime approvedAt = LocalDateTime.now();

    public PaymentEventFixtureBuilder addPaymentOrder(PaymentOrder paymentOrder) {
        paymentOrders.add(paymentOrder);
        orderName = paymentOrders.stream()
            .map(PaymentOrder::getProductName)
            .collect(Collectors.joining(", "));
        return this;
    }

    public PaymentEventFixtureBuilder withOrderId(OrderId orderId) {
        this.orderId = orderId;
        return this;
    }

    public PaymentEventFixtureBuilder withPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
        return this;
    }

    public PaymentEventFixtureBuilder withMethod(PaymentMethod method) {
        this.method = method;
        return this;
    }

    public PaymentEventFixtureBuilder withApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
        return this;
    }

    public PaymentEvent build() {
        PaymentEvent paymentEvent = PaymentEvent.builder()
            .buyerId(buyerId)
            .paymentOrders(paymentOrders)
            .orderName(orderName)
            .paymentKey(paymentKey)
            .orderId(orderId)
            .isPaymentDone(isPaymentDone)
            .method(method)
            .approvedAt(approvedAt)
            .build();

        paymentOrders.forEach(it -> it.assignPaymentEvent(paymentEvent));

        return paymentEvent;
    }

}
