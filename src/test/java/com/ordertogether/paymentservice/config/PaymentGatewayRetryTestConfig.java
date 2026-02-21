package com.ordertogether.paymentservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;

@TestConfiguration
public class PaymentGatewayRetryTestConfig {

    @Bean
    public RetryTemplate NeverRetryTemplate() {
        RetryPolicy neverRetryPolicy = RetryPolicy.builder()
            .maxRetries(0)
            .build();

        return new RetryTemplate(neverRetryPolicy);
    }

}
