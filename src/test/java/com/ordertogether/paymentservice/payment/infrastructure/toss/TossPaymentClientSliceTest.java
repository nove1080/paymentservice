package com.ordertogether.paymentservice.payment.infrastructure.toss;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.ordertogether.paymentservice.config.PaymentGatewayRetryTestConfig;
import com.ordertogether.paymentservice.exception.PaymentGatewayConfirmationException;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.infrastructure.toss.error.TossPaymentErrorCode;
import com.ordertogether.paymentservice.payment.service.command.PGConfirmCommand;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@DisplayName("토스페이먼츠 클라이언트 슬라이스 테스트")
@ActiveProfiles("test")
@Import(PaymentGatewayRetryTestConfig.class)
@RestClientTest
class TossPaymentClientSliceTest {

    private final MockRestServiceServer mockServer;
    private final TossPaymentClient tossPaymentClient;

    public TossPaymentClientSliceTest(
        @Autowired RestClient.Builder restClientBuilder,
        @Autowired RetryTemplate neverRetryTemplate,
        @Value("${pg.toss.url}") String baseUrl
    ) {
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        tossPaymentClient = new TossPaymentClient(
            restClientBuilder
                .baseUrl(baseUrl)
                .build(),
            neverRetryTemplate);
    }

    @Nested
    @DisplayName("결제 승인 과정 중")
    class WhenConfirmPayment {

        @DisplayName("토스 API 에러 응답을 성공/실패/재시도 가능으로 올바르게 분류할 수 있다")
        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("tossApiErrorScenario")
        void whenOccurError_thenClassifySuccessful(ErrorScenario errorResponse) {
            //given
            PGConfirmCommand command = new PGConfirmCommand(
                UUID.randomUUID().toString(),
                new OrderId(UUID.randomUUID().toString()),
                1000L
            );

            mockServer.expect(once(), requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(errorResponse.status())
                    .body(errorResponse.generateErrorResponse())
                    .contentType(MediaType.APPLICATION_JSON));

            //when & then
            assertThatThrownBy(() -> tossPaymentClient.confirmPayment(command))
                .isInstanceOf(PaymentGatewayConfirmationException.class)
                .satisfies(throwable -> {
                    PaymentGatewayConfirmationException ex = (PaymentGatewayConfirmationException) throwable;
                    assertThat(ex.getErrorCode()).isEqualTo(errorResponse.code());
                    assertThat(ex.getPaymentStatus().isSuccess()).isEqualTo(errorResponse.isSuccess());
                    assertThat(ex.isRetryable()).isEqualTo(errorResponse.isRetryable());
                    assertThat(ex.getPaymentStatus().isFail()).isEqualTo(errorResponse.isFail());
                });

            mockServer.verify();
        }

        private static Stream<Arguments> tossApiErrorScenario() {
            return Arrays.stream(TossPaymentErrorCode.values())
                .map(ErrorScenario::from)
                .map(s ->
                    Arguments.of(
                        Named.of(
                            "%s [isSuccess=%s, isFail=%s, retryable=%s]"
                                .formatted(s.code(), s.isSuccess(), s.isFail(), s.isRetryable()),
                            s
                        )
                    ));
        }

        @Builder
        record ErrorScenario(String code, String message, HttpStatus status, Boolean isSuccess, Boolean isRetryable, Boolean isFail) {

            public static ErrorScenario from(TossPaymentErrorCode errorCode) {
                return ErrorScenario.builder()
                    .code(errorCode.name())
                    .message(errorCode.getMessage())
                    .status(HttpStatus.valueOf(errorCode.getStatusCode()))
                    .isSuccess(errorCode.isSuccess())
                    .isRetryable(errorCode.isRetryable())
                    .isFail(errorCode.isFail())
                    .build();
            }

            public String generateErrorResponse() {
                return """
                    {
                        "code": "%s",
                        "message": "%s"
                    }
                    """.formatted(code, message);
            }
        }
    }

}
