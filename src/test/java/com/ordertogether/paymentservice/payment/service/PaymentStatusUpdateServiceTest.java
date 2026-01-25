package com.ordertogether.paymentservice.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.repository.PaymentRepository;
import com.ordertogether.paymentservice.payment.service.command.PaymentStatusUpdateCommand;
import com.ordertogether.paymentservice.support.fixture.PaymentEventFixtureBuilder;
import com.ordertogether.paymentservice.support.fixture.PaymentOrderFixtureBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("결제 상태 업데이트 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class PaymentStatusUpdateServiceTest {

    @InjectMocks
    private PaymentStatusUpdateService paymentStatusUpdateService;

    @Mock
    private PaymentRepository paymentRepository;

    @Nested
    @DisplayName("결제 상태 업데이트 시")
    class WhenUpdate {

        @Test
        @DisplayName("변경 이력을 기록하고, 상태를 변경한다")
        void givenNormalRequest_thenCreateUpdateHistoryRecordAndChangeStatus() {
            //given
            PaymentStatus currentStatus = PaymentStatus.NOT_STARTED;
            PaymentStatus nextStatus = PaymentStatus.EXECUTING;

            String paymentKey = "test-payment-key";
            OrderId orderId = OrderId.valueOf("test-order");

            PaymentEvent paymentEvent = new PaymentEventFixtureBuilder()
                .withOrderId(orderId)
                .addPaymentOrder(new PaymentOrderFixtureBuilder()
                    .withPaymentStatus(currentStatus)
                    .build())
                .addPaymentOrder(new PaymentOrderFixtureBuilder()
                    .withPaymentStatus(currentStatus)
                    .build())
                .build();

            willDoNothing().given(paymentRepository).insertPaymentHistory(any());
            given(paymentRepository.selectPaymentEvent(any()))
                .willReturn(paymentEvent);

            //when & then
            Assertions.assertDoesNotThrow(() -> paymentStatusUpdateService.updatePaymentStatus(
                PaymentStatusUpdateCommand.builder()
                    .paymentKey(paymentKey)
                    .orderId(orderId)
                    .status(PaymentStatus.EXECUTING)
                    .build()
            ));

            assertAll(
                () -> assertThat(paymentEvent.getPaymentKey()).isEqualTo(paymentKey),
                () -> paymentEvent.getPaymentOrders()
                    .forEach(it -> assertThat(it.getPaymentStatus()).isEqualTo(nextStatus)),
                () -> verify(paymentRepository, times(paymentEvent.getPaymentOrders().size()))
                    .insertPaymentHistory(any())
            );
        }
    }

}
