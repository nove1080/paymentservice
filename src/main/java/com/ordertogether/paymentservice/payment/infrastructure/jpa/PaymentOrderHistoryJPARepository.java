package com.ordertogether.paymentservice.payment.infrastructure.jpa;

import com.ordertogether.paymentservice.payment.domain.PaymentOrderHistory;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface PaymentOrderHistoryJPARepository extends JpaRepository<PaymentOrderHistory, Long> {

    @Query("""
        SELECT poh
        FROM PaymentOrderHistory poh
            JOIN PaymentOrder po on poh.paymentOrderId = po.id
            JOIN PaymentEvent pe on po.paymentEvent.id = pe.id
        WHERE pe.orderId = :orderId
    """)
    List<PaymentOrderHistory> findByOrderId(OrderId orderId);

}
