package com.ordertogether.paymentservice.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentFailure;
import com.ordertogether.paymentservice.payment.domain.PaymentMethod;
import com.ordertogether.paymentservice.payment.domain.PaymentOrder;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.repository.PaymentRepository;
import com.ordertogether.paymentservice.payment.service.command.PaymentStatusUpdateCommand;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult.SuccessExtraInfo;
import com.ordertogether.paymentservice.support.PaymentIntegrationTest;
import com.ordertogether.paymentservice.support.fixture.PaymentEventFixtureBuilder;
import com.ordertogether.paymentservice.support.fixture.PaymentOrderFixtureBuilder;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("결제 상태 업데이트 서비스 통합 테스트")
@ActiveProfiles(profiles = "test")
@SpringBootTest
class PaymentStatusUpdateServicePaymentIntegrationTest extends PaymentIntegrationTest {

    @Autowired
    private PaymentStatusUpdateService paymentStatusUpdateService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Nested
    @Transactional
    @DisplayName("결제 시작 상태로 업데이트 시")
    class WhenUpdateExecutingState {

        @Test
        @DisplayName("시작 상태로 변경하고 결제 키를 업데이트한다")
        void thenUpdatePaymentKeyAndState() {
            //given
            String paymentKey = "update-status-test-payment-key";
            OrderId orderId = OrderId.valueOf("update-status-test-order");

            PaymentEvent event = new PaymentEventFixtureBuilder()
                .withPaymentKey(null)
                .withOrderId(orderId)
                .addPaymentOrder(new PaymentOrderFixtureBuilder()
                    .withPaymentStatus(PaymentStatus.NOT_STARTED)
                    .build())
                .addPaymentOrder(new PaymentOrderFixtureBuilder()
                    .withPaymentStatus(PaymentStatus.NOT_STARTED)
                    .build())
                .build();

            paymentRepository.insertPaymentEvent(event);

            //when
            paymentStatusUpdateService.updatePaymentStatus(PaymentStatusUpdateCommand.builder()
                .orderId(orderId)
                .paymentKey(paymentKey)
                .status(PaymentStatus.EXECUTING)
                .build());

            //then
            PaymentEvent executedEvent = paymentRepository.selectPaymentEvent(orderId);
            assertAll(
                () -> assertThat(executedEvent.getPaymentKey()).isEqualTo(paymentKey),
                () -> assertThat(executedEvent.getPaymentOrders())
                    .extracting(PaymentOrder::getPaymentStatus)
                    .containsOnly(PaymentStatus.EXECUTING)
            );
        }

    }

    @Nested
    @Transactional
    @DisplayName("결제 성공 상태로 업데이트 시")
    class WhenUpdateSuccessState {

        @Test
        @DisplayName("결제를 완료 처리하고 부가 정보를 기록한다")
        void thenUpdatePaymentOrdersToSuccessAndMarkPaymentDone() {
            //given
            OrderId orderId = OrderId.valueOf("update-success-test-order");

            PaymentEvent event = new PaymentEventFixtureBuilder()
                .withPaymentKey("initial-payment-key")
                .withOrderId(orderId)
                .withMethod(null)
                .withApprovedAt(null)
                .addPaymentOrder(new PaymentOrderFixtureBuilder()
                    .withPaymentStatus(PaymentStatus.EXECUTING)
                    .build())
                .addPaymentOrder(new PaymentOrderFixtureBuilder()
                    .withPaymentStatus(PaymentStatus.EXECUTING)
                    .build())
                .build();

            paymentRepository.insertPaymentEvent(event);

            //when
            paymentStatusUpdateService.updatePaymentStatus(PaymentStatusUpdateCommand.builder()
                .orderId(orderId)
                .status(PaymentStatus.SUCCESS)
                .successExtraInfo(SuccessExtraInfo.builder()
                    .paymentMethod(PaymentMethod.CARD)
                    .approvedAt(LocalDateTime.now())
                    .build())
                .build());

            //then
            PaymentEvent succeedEvent = paymentRepository.selectPaymentEvent(orderId);
            assertAll(
                () -> assertThat(succeedEvent.isPaymentDone()).isTrue(),
                () -> assertThat(succeedEvent.getApprovedAt()).isNotNull(),
                () -> assertThat(succeedEvent.getPaymentOrders())
                    .extracting(PaymentOrder::getPaymentStatus)
                    .containsOnly(PaymentStatus.SUCCESS)
            );
        }
    }

    @Nested
    @Transactional
    @DisplayName("결제 실패 상태로 업데이트 시")
    class WhenUpdateFailState {

        @Test
        @DisplayName("결제 주문들의 상태가 FAIL 로 변경된다")
        void thenUpdatePaymentOrdersToFail() {
            //given
            OrderId orderId = OrderId.valueOf("update-fail-test-order");

            PaymentEvent event = new PaymentEventFixtureBuilder()
                .withPaymentKey("initial-payment-key")
                .withOrderId(orderId)
                .addPaymentOrder(new PaymentOrderFixtureBuilder()
                    .withPaymentStatus(PaymentStatus.EXECUTING)
                    .build())
                .addPaymentOrder(new PaymentOrderFixtureBuilder()
                    .withPaymentStatus(PaymentStatus.EXECUTING)
                    .build())
                .build();

            paymentRepository.insertPaymentEvent(event);

            //when
            paymentStatusUpdateService.updatePaymentStatus(PaymentStatusUpdateCommand.builder()
                .orderId(orderId)
                .status(PaymentStatus.FAIL)
                .failureExtraInfo(
                    PaymentFailure.builder()
                        .code("PAYMENT_FAILED_TEST_CODE")
                        .message("PAYMENT_FAILED_TEST_MESSAGE")
                        .build()
                )
                .build());

            //then
            PaymentEvent failedEvent = paymentRepository.selectPaymentEvent(orderId);
            assertThat(failedEvent.getPaymentOrders())
                .extracting(PaymentOrder::getPaymentStatus)
                .containsOnly(PaymentStatus.FAIL);
        }
    }

}
