package com.ordertogether.paymentservice.support.database;

import org.springframework.transaction.annotation.Transactional;

public class TransactionExecutor {

    @Transactional
    public void invoke(Runnable runnable) {
        runnable.run();
    }

}
