package com.ordertogether.paymentservice.payment.infrastructure.jpa;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface PaymentEventJPARepository extends JpaRepository<PaymentEvent, Long> {

    @Query("SELECT pe FROM PaymentEvent pe WHERE pe.orderId.value = :orderId")
    Optional<PaymentEvent> findByOrderId(String orderId);

    @Query("""
        SELECT DISTINCT pe
        FROM PaymentEvent pe
            JOIN FETCH pe.paymentOrders po
        WHERE pe.failedCount < :failedCountThreshold
            AND (po.paymentStatus = 'UNKNOWN' OR (po.paymentStatus = 'EXECUTING' AND po.updatedAt <= :updatedBefore))
    """)
    List<PaymentEvent> findRecoverablePaymentEvents(Integer failedCountThreshold, LocalDateTime updatedBefore);
}
