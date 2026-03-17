package com.ordertogether.paymentservice.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentFailure;
import com.ordertogether.paymentservice.payment.domain.PaymentMethod;
import com.ordertogether.paymentservice.payment.domain.PaymentOrderHistory;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.domain.vo.Price;
import com.ordertogether.paymentservice.payment.infrastructure.toss.error.TossPaymentErrorCode;
import com.ordertogether.paymentservice.payment.repository.PaymentRepository;
import com.ordertogether.paymentservice.payment.service.command.PaymentConfirmCommand;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult.SuccessExtraInfo;
import com.ordertogether.paymentservice.payment.service.result.PaymentConfirmResult;
import com.ordertogether.paymentservice.support.base.PaymentIntegrationTest;
import com.ordertogether.paymentservice.support.database.TransactionExecutor;
import com.ordertogether.paymentservice.support.fixture.PaymentEventFixtureBuilder;
import com.ordertogether.paymentservice.support.fixture.PaymentOrderFixtureBuilder;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DisplayName("결제 승인 서비스 통합 테스트")
@ActiveProfiles(profiles = "test")
class PaymentConfirmServiceIntegrationTest extends PaymentIntegrationTest {

    @Autowired
    private PaymentConfirmService paymentConfirmService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TransactionExecutor transactionExecutor;

    @MockitoBean
    private PaymentGatewayClient paymentGatewayClient;

    private final OrderId orderId = OrderId.valueOf("test_order_id");
    private final String paymentKey = "test_payment_key";
    private final PaymentEvent paymentEvent = new PaymentEventFixtureBuilder()
        .withPaymentKey(null)
        .withMethod(null)
        .withOrderId(orderId)
        .withApprovedAt(null)
        .addPaymentOrder(new PaymentOrderFixtureBuilder()
            .withAmount(Price.valueOf(10000L))
            .withPaymentStatus(PaymentStatus.NOT_STARTED)
            .build())
        .addPaymentOrder(new PaymentOrderFixtureBuilder()
            .withAmount(Price.valueOf(20000L))
            .withPaymentStatus(PaymentStatus.NOT_STARTED)
            .build())
        .build();

    @BeforeEach
    void saveNotStartedPayment() {
        paymentRepository.insertPaymentEvent(paymentEvent);
    }

    @Nested
    @DisplayName("confirm()")
    class Confirm {

        @Nested
        @DisplayName("올바른 요청 시")
        class WhenValidRequest {

            @Test
            @DisplayName("결제를 승인한다")
            void thenConfirmPayment() {
                //given
                PaymentConfirmCommand command = PaymentConfirmCommand.builder()
                    .paymentKey(paymentKey)
                    .orderId(orderId)
                    .amount(30000L)
                    .build();

                given(paymentGatewayClient.confirmPayment(any()))
                    .willReturn(PGConfirmResult.builder()
                        .paymentKey(paymentKey)
                        .orderId(orderId)
                        .status(PaymentStatus.SUCCESS)
                        .successExtraInfo(SuccessExtraInfo.builder()
                            .approvedAt(LocalDateTime.now())
                            .amount(paymentEvent.totalAmount())
                            .paymentMethod(PaymentMethod.CARD)
                            .build())
                        .build());

                //when
                PaymentConfirmResult confirmResult = paymentConfirmService.confirm(command);

                //then
                assertThat(confirmResult.paymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
            }

        }

        @Nested
        @DisplayName("결제 금액이 일치하지 않으면")
        class WhenAmountIsInvalid {

            @Test
            @DisplayName("결제 승인을 거부한다")
            void thenSavePaymentStatusFail() {
                //given
                PaymentConfirmCommand command = PaymentConfirmCommand.builder()
                    .paymentKey(paymentKey)
                    .orderId(orderId)
                    .amount(40000L)
                    .build();

                //when
                PaymentConfirmResult confirmResult = paymentConfirmService.confirm(command);

                //then
                assertThat(confirmResult.paymentStatus().isFail()).isTrue();
                transactionExecutor.invoke(
                    () -> {
                        PaymentEvent failedPaymentEvent = paymentRepository.selectPaymentEvent(orderId);
                        assertThat(failedPaymentEvent.isFail()).isTrue();
                    }
                );
            }
        }

        @Nested
        @DisplayName("PG사의 결제 승인에 실패한 경우")
        class WhenPaymentConfirmFail {

            @Test
            @DisplayName("결제 승인에 실패하고, 결제 상태와 실패 사유를 기록한다")
            void givenPaymentGatewayFailResponse_thenFail() {
                //given
                PaymentConfirmCommand command = PaymentConfirmCommand.builder()
                    .paymentKey(paymentKey)
                    .orderId(orderId)
                    .amount(30000L)
                    .build();

                TossPaymentErrorCode errorCode = TossPaymentErrorCode.FDS_ERROR;
                given(paymentGatewayClient.confirmPayment(any()))
                    .willReturn(PGConfirmResult.builder()
                        .paymentKey(paymentKey)
                        .orderId(orderId)
                        .status(PaymentStatus.FAIL)
                        .failureExtraInfo(PaymentFailure.builder()
                            .code(errorCode.name())
                            .message(errorCode.getMessage())
                            .build())
                        .build());

                //when
                PaymentConfirmResult confirmResult = paymentConfirmService.confirm(command);

                //then
                assertThat(confirmResult.paymentStatus().isFail()).isTrue();
                transactionExecutor.invoke(
                    () -> {
                        PaymentEvent failedPaymentEvent = paymentRepository.selectPaymentEvent(orderId);
                        List<PaymentOrderHistory> paymentOrderHistories = paymentRepository.selectPaymentHistories(orderId);

                        assertAll(
                            () -> assertThat(failedPaymentEvent.isFail()).isTrue(),
                            () -> assertThat(paymentOrderHistories)
                                .filteredOn(it -> it.getPreviousStatus().isExecuting())
                                .filteredOn(it -> it.getCurrentStatus().isFail())
                                .allSatisfy(it -> assertThat(it.getReason()).isEqualTo(errorCode.getMessage()))
                                .hasSize(paymentEvent.totalQuantity())
                        );
                    }
                );
            }
        }

    }

}
