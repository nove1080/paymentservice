package com.ordertogether.paymentservice.support.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

public class PaymentDatabaseHelper {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void clean() {
        deleteAllPaymentEvent();
        deleteAllPaymentOrder();
        deleteAllPaymentOrderHistory();
    }

    private int deleteAllPaymentEvent() {
        return entityManager.createNativeQuery("DELETE FROM payment_event").executeUpdate();
    }

    private int deleteAllPaymentOrder() {
        return entityManager.createNativeQuery("DELETE FROM payment_order").executeUpdate();
    }

    private int deleteAllPaymentOrderHistory() {
        return entityManager.createNativeQuery("DELETE FROM payment_order_history").executeUpdate();
    }

}
