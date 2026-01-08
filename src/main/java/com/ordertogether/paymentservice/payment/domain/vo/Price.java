package com.ordertogether.paymentservice.payment.domain.vo;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public record Price (
    BigDecimal value
) {
    private static final BigDecimal MIN_PRICE = BigDecimal.ZERO;

    public Price {
        Objects.requireNonNull(value, "Price 는 null 일 수 없습니다.");
        validateRange(value);
    }

    public static Price valueOf(long value) {
        return new Price(BigDecimal.valueOf(value));
    }

    private void validateRange(BigDecimal value) {
        if (value.compareTo(MIN_PRICE) < 0) {
            throw new IllegalArgumentException("Price 는 음수일 수 없습니다. [값: %s]".formatted(value));
        }
    }

    public Long toLong() {
        return value.longValue();
    }
}
