package com.ordertogether.paymentservice.support.fixture;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentOrder;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.vo.Price;
import java.math.BigDecimal;

public class PaymentOrderFixtureBuilder {

    private PaymentEvent paymentEvent;

    private Long sellerId = 1L;

    private Long productId = 1L;

    private String productName = "test_product_001";

    private Price amount = new Price(BigDecimal.valueOf(10000));

    private PaymentStatus paymentStatus = PaymentStatus.NOT_STARTED;

    public PaymentOrderFixtureBuilder withAmount(Price amount) {
        this.amount = amount;
        return this;
    }

    public PaymentOrderFixtureBuilder withPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public PaymentOrder build() {
        return PaymentOrder.builder()
            .paymentEvent(paymentEvent)
            .sellerId(sellerId)
            .productId(productId)
            .productName(productName)
            .amount(amount)
            .paymentStatus(paymentStatus)
            .build();
    }

}
