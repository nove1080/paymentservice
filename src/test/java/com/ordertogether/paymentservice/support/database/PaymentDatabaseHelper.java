package com.ordertogether.paymentservice.support.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

public class PaymentDatabaseHelper {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void clean() {
        entityManager.createNativeQuery("DELETE FROM payment_order_history");
        entityManager.createNativeQuery("DELETE FROM payment_order");
        entityManager.createNativeQuery("DELETE FROM payment_event");
    }

}
