package com.ordertogether.paymentservice.payment.repository;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentOrderHistory;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;

public interface PaymentRepository {

    PaymentEvent selectPaymentEvent(OrderId orderId);

    void insertPaymentEvent(PaymentEvent paymentEvent);

    void insertPaymentHistory(PaymentOrderHistory history);
}
