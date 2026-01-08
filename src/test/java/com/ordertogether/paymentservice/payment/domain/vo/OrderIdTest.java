package com.ordertogether.paymentservice.payment.domain.vo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("OrderId 테스트")
class OrderIdTest {

    @Nested
    @DisplayName("생성 시")
    class WhenCreate {

        @Nested
        @DisplayName("길이 검증")
        class validateLength {
            @Test
            @DisplayName("null 이면 예외를 던진다")
            void givenNullValue_thenThrowException() {
                // when & then
                assertThatThrownBy(() -> new OrderId(null))
                    .isInstanceOf(NullPointerException.class);
            }

            @Test
            @DisplayName("6자 미만이면 예외를 던진다")
            void givenTooShortValue_thenThrowException() {
                // given
                String shortValue = "12345";

                // when & then
                assertThatThrownBy(() -> new OrderId(shortValue))
                    .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("64자 초과이면 예외를 던진다")
            void givenTooLongValue_thenThrowException() {
                // given
                String longValue = "a".repeat(65);

                // when & then
                assertThatThrownBy(() -> new OrderId(longValue))
                    .isInstanceOf(IllegalArgumentException.class);
            }

        }

        @Nested
        @DisplayName("형식 검증")
        class validateFormat {
            @ParameterizedTest(name = "[{index}] {0}")
            @ValueSource(strings = {
                "validID123",
                "Valid_ID-456",
                "anotherValidID_789-0",
                "A1b2C3d4E5f6G7h8I9j0-K_L"
            })
            @DisplayName("유효한 문자로 구성되면 생성된다")
            void givenValidInput_thenCreateSuccess(String input) {
                // when & then
                assertDoesNotThrow(() -> new OrderId(input));
                assertEquals(input, new OrderId(input).value());
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @ValueSource(strings = {
                "Invalid ID!",    // 공백 및 특수문자 포함
                "InvalidID!",     // 특수문자 포함
                "Invalid ID",     // 공백 포함
            })
            @DisplayName("유효하지 않은 문자가 있으면 예외를 던진다")
            void givenInvalidInput_thenThrowException(String input) {
                // when & then
                assertThatThrownBy(() -> new OrderId(input))
                    .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

}
