package com.ordertogether.paymentservice.config;

import com.ordertogether.paymentservice.support.database.PaymentDatabaseHelper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class PaymentTestConfig {

    @Bean
    public PaymentDatabaseHelper paymentDatabaseHelper() {
        return new PaymentDatabaseHelper();
    }

}
