package com.ordertogether.paymentservice.payment.domain.vo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Price 테스트")
class PriceTest {

    @Nested
    @DisplayName("생성 시")
    class WhenCreate {
        @Test
        @DisplayName("음수 값이 주어지면 예외를 던진다")
        void givenNegativeValue_thenThrowException() {
            // given
            long negativeValue = -1L;

            // when & then
            assertThatThrownBy(() -> Price.valueOf(negativeValue))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

}
