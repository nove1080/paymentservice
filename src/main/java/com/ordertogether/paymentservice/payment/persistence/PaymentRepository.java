package com.ordertogether.paymentservice.payment.persistence;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository {

    PaymentEvent selectPaymentEvent(String orderId);

    void insertPaymentEvent(PaymentEvent paymentEvent);

}
