package com.ordertogether.paymentservice.payment.persistence.jpa;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.persistence.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryJPAAdapter implements PaymentRepository {

    private final PaymentEventJPARepository paymentEventJPARepository;

    @Override
    public PaymentEvent selectPaymentEvent(OrderId orderId) {
        return paymentEventJPARepository.findByOrderId(orderId.value())
            .orElseThrow(() -> new EntityNotFoundException("[value: %s]에 해당하는 PaymentEvent 를 찾을 수 없습니다.".formatted(orderId)));
    }

    @Override
    public void insertPaymentEvent(PaymentEvent paymentEvent) {
        paymentEventJPARepository.save(paymentEvent);
    }
}
