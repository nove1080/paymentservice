package com.ordertogether.paymentservice.payment.repository;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentEventJPARepository extends JpaRepository<PaymentEvent, Long> {
}
