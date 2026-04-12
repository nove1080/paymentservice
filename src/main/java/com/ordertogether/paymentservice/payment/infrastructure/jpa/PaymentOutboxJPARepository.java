package com.ordertogether.paymentservice.payment.infrastructure.jpa;

import com.ordertogether.paymentservice.payment.domain.PaymentOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentOutboxJPARepository extends JpaRepository<PaymentOutbox, Long> {

}
