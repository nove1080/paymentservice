package com.ordertogether.paymentservice.common.config;

import com.ordertogether.paymentservice.exception.PaymentGatewayResponseException;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;

@Configuration
public class PaymentGatewayRetryConfiguration {

    private static final int MAX_RETRIES = 3;

    private static final Duration INITIAL_DELAY = Duration.ofMillis(100);
    private static final Duration JITTER = Duration.ofMillis(10);
    private static final double MULTIPLIER = 2.0;
    private static final Duration MAX_DELAY = Duration.ofSeconds(1);

    @Bean
    public RetryTemplate paymentGatewayRetryTemplate() {
        RetryPolicy retryPolicy = RetryPolicy.builder()
            .includes(PaymentGatewayResponseException.class)
            .predicate(this::isRetryablePaymentGatewayException)
            .maxRetries(MAX_RETRIES)
            .delay(INITIAL_DELAY)
            .jitter(JITTER)
            .multiplier(MULTIPLIER)
            .maxDelay(MAX_DELAY)
            .build();

        return new RetryTemplate(retryPolicy);
    }

    private boolean isRetryablePaymentGatewayException(Throwable throwable) {
        return throwable instanceof PaymentGatewayResponseException ex
            && Boolean.TRUE.equals(ex.isRetryable());
    }
}
