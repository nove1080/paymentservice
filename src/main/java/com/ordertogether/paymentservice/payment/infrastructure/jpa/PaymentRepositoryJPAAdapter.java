package com.ordertogether.paymentservice.payment.infrastructure.jpa;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentOrderHistory;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class PaymentRepositoryJPAAdapter implements PaymentRepository {

    private final PaymentEventJPARepository paymentEventJPARepository;
    private final PaymentOrderHistoryJPARepository paymentOrderHistoryRepository;

    @Override
    public PaymentEvent selectPaymentEvent(OrderId orderId) {
        return paymentEventJPARepository.findByOrderId(orderId.value())
            .orElseThrow(() -> new EntityNotFoundException("[value: %s]에 해당하는 PaymentEvent 를 찾을 수 없습니다.".formatted(orderId)));
    }

    @Override
    @Transactional
    public void insertPaymentEvent(PaymentEvent paymentEvent) {
        paymentEventJPARepository.save(paymentEvent);
    }

    @Override
    @Transactional
    public void insertPaymentHistory(PaymentOrderHistory history) {
        paymentOrderHistoryRepository.save(history);
    }
}
