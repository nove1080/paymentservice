package com.ordertogether.paymentservice.payment.service;

import com.ordertogether.paymentservice.payment.domain.LedgerEventMessage;
import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.WalletEventMessage;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentCompleteService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public void completePayment(WalletEventMessage message) {
        PaymentEvent paymentEvent = paymentRepository.selectPaymentEvent(OrderId.valueOf(message.orderId()));
        paymentEvent.completeWalletUpdate();
        paymentEvent.markAsDoneIfAllComplete();
    }

    @Transactional
    public void completePayment(LedgerEventMessage message) {
        PaymentEvent paymentEvent = paymentRepository.selectPaymentEvent(OrderId.valueOf(message.orderId()));
        paymentEvent.completeLedgerUpdate();
        paymentEvent.markAsDoneIfAllComplete();
    }

}
