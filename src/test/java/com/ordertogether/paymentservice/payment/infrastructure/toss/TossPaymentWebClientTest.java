package com.ordertogether.paymentservice.payment.infrastructure.toss;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ordertogether.paymentservice.common.config.PaymentGatewayRetryConfiguration;
import com.ordertogether.paymentservice.config.PaymentGatewayTestClientConfig;
import com.ordertogether.paymentservice.exception.PaymentRetryExhaustedException;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.infrastructure.toss.error.TossPaymentErrorCode;
import com.ordertogether.paymentservice.payment.service.command.PGConfirmCommand;
import com.ordertogether.paymentservice.support.constant.TestType;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.test.context.ActiveProfiles;

@Tag(TestType.LONG_TIME_TEST)
@DisplayName("토스페이먼츠 클라이언트 테스트")
@ActiveProfiles("test")
@SpringBootTest(classes = {
    PaymentGatewayTestClientConfig.class,
    PaymentGatewayRetryConfiguration.class}
)
class TossPaymentWebClientTest {

    private static final String TOSS_ERROR_SCENARIO_HEADER = "TossPayments-Test-Code";

    @Autowired
    private PaymentGatewayTestClientConfig paymentGatewayTestClientConfig;

    @Autowired
    private RetryTemplate paymentGatewayRetryTemplate;

    @Nested
    @DisplayName("복구 가능한 에러가 발생한 경우")
    class WhenRetryableErrorOccurs {

        @DisplayName("재시도를 수행한다")
        @ParameterizedTest
        @MethodSource("tossRetryableErrorCodes")
        void whenOccurError_thenClassifyRetryableError(TossPaymentErrorCode errorCode) {
            //given
            TossPaymentWebClient tossPaymentWebClient = new TossPaymentWebClient(
                paymentGatewayTestClientConfig.createWithHeaders(
                    Map.of(TOSS_ERROR_SCENARIO_HEADER, errorCode.name())
                ),
                paymentGatewayRetryTemplate
            );

            PGConfirmCommand confirmCommand = PGConfirmCommand.builder()
                .paymentKey(UUID.randomUUID().toString())
                .orderId(OrderId.valueOf(UUID.randomUUID().toString()))
                .amount(1000L)
                .build();

            //when & then
            assertThatThrownBy(() -> tossPaymentWebClient.confirmPayment(confirmCommand))
                .isInstanceOf(PaymentRetryExhaustedException.class)
                .satisfies(throwable -> {
                    PaymentRetryExhaustedException ex = (PaymentRetryExhaustedException) throwable;
                    assertThat(ex.getErrorCode()).isEqualTo(errorCode.name());
                    assertThat(ex.getRetryCount()).isPositive();
                });
        }

        private static Stream<Arguments> tossRetryableErrorCodes() {
            return Arrays.stream(TossPaymentErrorCode.values())
                .filter(TossPaymentErrorCode::isRetryable)
                .map(Arguments::of);
        }
    }

}
