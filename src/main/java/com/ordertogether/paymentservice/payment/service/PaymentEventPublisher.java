package com.ordertogether.paymentservice.payment.service;

import com.ordertogether.paymentservice.payment.domain.PaymentConfirmMessage;

public interface PaymentEventPublisher {

    void publishPaymentConfirmedEvent(PaymentConfirmMessage message);

}
