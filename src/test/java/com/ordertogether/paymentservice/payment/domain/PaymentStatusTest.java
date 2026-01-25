package com.ordertogether.paymentservice.payment.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("결제 상태 테스트")
class PaymentStatusTest {

    @Nested
    @DisplayName("상태 전이 시")
    class whenTransit {
        @DisplayName("잘못된 상태 전이면 false 를 반환한다")
        @MethodSource("invalidTransitions")
        @ParameterizedTest(name = "{0} -> {1}")
        void thenThrowException(PaymentStatus current, PaymentStatus next) {
            // when & then
            assertThat(current.canTransitionTo(next)).isFalse();
        }

        static Stream<Arguments> invalidTransitions() {
            return Stream.of(
                namedArguments(PaymentStatus.NOT_STARTED, PaymentStatus.SUCCESS),
                namedArguments(PaymentStatus.NOT_STARTED, PaymentStatus.FAIL),

                namedArguments(PaymentStatus.EXECUTING, PaymentStatus.NOT_STARTED),

                namedArguments(PaymentStatus.SUCCESS, PaymentStatus.NOT_STARTED),
                namedArguments(PaymentStatus.SUCCESS, PaymentStatus.EXECUTING),
                namedArguments(PaymentStatus.SUCCESS, PaymentStatus.FAIL),
                namedArguments(PaymentStatus.SUCCESS, PaymentStatus.UNKNOWN),

                namedArguments(PaymentStatus.FAIL, PaymentStatus.NOT_STARTED),
                namedArguments(PaymentStatus.FAIL, PaymentStatus.EXECUTING),
                namedArguments(PaymentStatus.FAIL, PaymentStatus.SUCCESS),
                namedArguments(PaymentStatus.FAIL, PaymentStatus.UNKNOWN),

                namedArguments(PaymentStatus.UNKNOWN, PaymentStatus.NOT_STARTED),
                namedArguments(PaymentStatus.UNKNOWN, PaymentStatus.SUCCESS),
                namedArguments(PaymentStatus.UNKNOWN, PaymentStatus.FAIL)
            );
        }

        private static Arguments namedArguments(PaymentStatus current, PaymentStatus next) {
            return Arguments.of(
                Named.of(current.name(), current),
                Named.of(next.name(), next)
            );
        }
    }

}
