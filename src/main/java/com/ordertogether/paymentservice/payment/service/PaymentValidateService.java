package com.ordertogether.paymentservice.payment.service;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.repository.PaymentRepository;
import com.ordertogether.paymentservice.exception.InvalidPaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentValidateService {

    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public void validateAmount(OrderId orderId, Long amount) {
        PaymentEvent paymentEvent = paymentRepository.selectPaymentEvent(orderId);
        Long expectedAmount = paymentEvent.totalAmount();
        if (!expectedAmount.equals(amount)) {
            throw new InvalidPaymentException(orderId, expectedAmount, amount);
        }
    }

}
