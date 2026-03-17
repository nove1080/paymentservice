package com.ordertogether.paymentservice.payment.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "payment.recovery")
public class PaymentRecoveryProperties {

    private final Boolean enabled;
    private final Integer maxRetryCount;
    private final Integer retryIntervalMinutes;

    @ConstructorBinding
    public PaymentRecoveryProperties(Boolean enabled, Integer maxRetryCount, Integer retryIntervalMinutes) {
        this.enabled = enabled;
        this.maxRetryCount = maxRetryCount;
        this.retryIntervalMinutes = retryIntervalMinutes;
    }
}
