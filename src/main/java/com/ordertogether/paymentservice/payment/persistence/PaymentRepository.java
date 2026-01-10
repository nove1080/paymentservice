package com.ordertogether.paymentservice.payment.persistence;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;

public interface PaymentRepository {

    PaymentEvent selectPaymentEvent(OrderId orderId);

    void insertPaymentEvent(PaymentEvent paymentEvent);

}
