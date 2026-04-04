package com.ordertogether.paymentservice.payment.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentFailure;
import com.ordertogether.paymentservice.payment.domain.PaymentMethod;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.repository.PaymentRepository;
import com.ordertogether.paymentservice.payment.service.command.CheckoutCommand;
import com.ordertogether.paymentservice.payment.service.command.PaymentStatusUpdateCommand;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult.SuccessExtraInfo;
import com.ordertogether.paymentservice.support.base.PaymentIntegrationTest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("결제 복구 서비스 통합 테스트")
@ActiveProfiles(profiles = "test")
class PaymentRecoveryServiceIntegrationTest extends PaymentIntegrationTest {

    @Autowired
    private PaymentRecoveryService paymentRecoveryService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private PaymentStatusUpdateService paymentStatusUpdateService;

    @MockitoBean
    private PaymentGatewayClient paymentGatewayClient;

    @Test
    @Transactional
    @DisplayName("결제 상태를 알 수 없는 이벤트를 복구할 수 있다")
    void givenRecoveryEvent_thenShouldRecovery() {
        //given
        OrderId orderId = OrderId.from(UUID.randomUUID().toString());
        String paymentKey = UUID.randomUUID().toString();

        createUnknownPaymentEvent(orderId, paymentKey);

        given(paymentGatewayClient.confirmPayment(any()))
            .willReturn(PGConfirmResult.builder()
                .orderId(orderId)
                .paymentKey(paymentKey)
                .successExtraInfo(SuccessExtraInfo.builder()
                    .paymentMethod(PaymentMethod.CARD)
                    .approvedAt(LocalDateTime.now())
                    .build())
                .status(PaymentStatus.SUCCESS)
                .build());

        //when
        paymentRecoveryService.recovery();

        //then
        PaymentEvent recoveredEvent = paymentRepository.selectPaymentEvent(orderId);
        assertTrue(recoveredEvent.isPaymentDone());
    }

    private void createUnknownPaymentEvent(OrderId orderId, String paymentKey) {
        checkoutService.checkout(CheckoutCommand.builder()
            .productIds(List.of(1L, 2L))
            .buyerId(1L)
            .idempotencyKey(orderId.value())
            .build());

        paymentStatusUpdateService.updatePaymentStatus(PaymentStatusUpdateCommand.builder()
            .orderId(orderId)
            .paymentKey(paymentKey)
            .status(PaymentStatus.EXECUTING)
            .build());

        paymentStatusUpdateService.updatePaymentStatus(PaymentStatusUpdateCommand.builder()
            .orderId(orderId)
            .paymentKey(paymentKey)
            .status(PaymentStatus.UNKNOWN)
            .failureExtraInfo(PaymentFailure.builder()
                .code("TEST_FAILURE")
                .message("결제 테스트 실패")
                .build())
            .build());
    }

}
