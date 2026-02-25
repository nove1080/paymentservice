package com.ordertogether.paymentservice.exception.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import com.ordertogether.paymentservice.exception.InvalidPaymentException;
import com.ordertogether.paymentservice.exception.PaymentGatewayConfirmationException;
import com.ordertogether.paymentservice.payment.domain.PaymentFailure;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.service.PaymentStatusUpdateService;
import com.ordertogether.paymentservice.payment.service.command.PaymentConfirmCommand;
import com.ordertogether.paymentservice.payment.service.result.PaymentConfirmResult;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("결제 승인 예외 핸들러 단위 테스트")
@ExtendWith(MockitoExtension.class)
class PaymentConfirmExceptionHandlerTest {

    private static final OrderId ORDER_ID = OrderId.valueOf("test_order_id");
    private static final String PAYMENT_KEY = "test_payment_key";

    @InjectMocks
    private PaymentConfirmExceptionHandler exceptionHandler;
    @Mock
    private PaymentStatusUpdateService paymentStatusUpdateService;

    @ParameterizedTest(name = "[{index}] {0} 인 경우 결제 상태 {1} 로 처리된다.")
    @MethodSource("errorScenarios")
    @DisplayName("예외 유형에 따라 적절히 핸들링 할 수 있다.")
    void whenErrorOccur_then(Throwable error, PaymentStatus expectedStatus) {
        //given
        PaymentConfirmCommand command = PaymentConfirmCommand.builder()
            .paymentKey(PAYMENT_KEY)
            .orderId(ORDER_ID)
            .amount(30000L)
            .build();

        //when
        PaymentConfirmResult result = exceptionHandler.handle(command, error);

        //then
        assertAll(
            () -> assertThat(result.paymentStatus()).isEqualTo(expectedStatus),
            () -> assertThat(result.failure()).isEqualTo(PaymentFailure.from(error))
        );

        verify(paymentStatusUpdateService).updatePaymentStatus(
            argThat(updateCommand -> updateCommand.status().equals(expectedStatus))
        );
    }


    static Stream<Arguments> errorScenarios() {
        return Stream.of(
            Arguments.of(
                new InvalidPaymentException(""),
                PaymentStatus.FAIL
            ),
            Arguments.of(
                new PaymentGatewayConfirmationException(PAYMENT_KEY, ORDER_ID, PaymentStatus.FAIL, "", ""),
                PaymentStatus.FAIL
            ),
            Arguments.of(
                new RuntimeException(""),
                PaymentStatus.UNKNOWN
            )
        );
    }

}
