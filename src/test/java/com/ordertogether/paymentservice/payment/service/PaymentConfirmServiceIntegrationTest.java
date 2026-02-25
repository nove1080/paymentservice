package com.ordertogether.paymentservice.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentFailure;
import com.ordertogether.paymentservice.payment.domain.PaymentMethod;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.domain.vo.Price;
import com.ordertogether.paymentservice.payment.repository.PaymentRepository;
import com.ordertogether.paymentservice.payment.service.command.PaymentConfirmCommand;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult.SuccessExtraInfo;
import com.ordertogether.paymentservice.payment.service.result.PaymentConfirmResult;
import com.ordertogether.paymentservice.support.base.PaymentIntegrationTest;
import com.ordertogether.paymentservice.support.fixture.PaymentEventFixtureBuilder;
import com.ordertogether.paymentservice.support.fixture.PaymentOrderFixtureBuilder;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("결제 승인 서비스 통합 테스트")
@ActiveProfiles(profiles = "test")
class PaymentConfirmServiceIntegrationTest extends PaymentIntegrationTest {

    @Autowired
    private PaymentConfirmService paymentConfirmService;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private PaymentGatewayClient paymentGatewayClient;

    @Nested
    @DisplayName("결제 승인 요청 시")
    class WhenConfirmRequested {

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

        @Test
        @DisplayName("결제 승인이 정상 처리된다")
        void thenSuccess() {
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

        @Test
        @Transactional
        @DisplayName("PG사 결제 승인에 실패한 경우, 결제 상태가 실패로 기록된다")
        void givenPaymentGatewayFailResponse_thenFail() {
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
                    .status(PaymentStatus.FAIL)
                    .failureExtraInfo(PaymentFailure.builder()
                        .code("")
                        .message("")
                        .build())
                    .build());

            //when
            PaymentConfirmResult confirmResult = paymentConfirmService.confirm(command);

            //then
            PaymentEvent failedPaymentEvent = paymentRepository.selectPaymentEvent(orderId);

            assertThat(confirmResult.paymentStatus().isFail()).isTrue();
            assertThat(failedPaymentEvent.isFail()).isTrue();
        }

        @Test
        @Transactional
        @DisplayName("결제 금액이 상이한 경우, 결제 상태가 실패로 기록된다")
        void whenInvalidPaymentErrorOccur_thenSavePaymentStatusFail() {
            //given
            PaymentConfirmCommand command = PaymentConfirmCommand.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(40000L)
                .build();

            //when
            PaymentConfirmResult confirmResult = paymentConfirmService.confirm(command);

            //then
            PaymentEvent failedPaymentEvent = paymentRepository.selectPaymentEvent(orderId);

            assertThat(confirmResult.paymentStatus().isFail()).isTrue();
            assertThat(failedPaymentEvent.isFail()).isTrue();
        }

    }

}
