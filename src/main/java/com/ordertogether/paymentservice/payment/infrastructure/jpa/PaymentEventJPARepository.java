package com.ordertogether.paymentservice.payment.infrastructure.jpa;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface PaymentEventJPARepository extends JpaRepository<PaymentEvent, Long> {

    @Query("SELECT pe FROM PaymentEvent pe WHERE pe.orderId.value = :orderId")
    Optional<PaymentEvent> findByOrderId(String orderId);
}
