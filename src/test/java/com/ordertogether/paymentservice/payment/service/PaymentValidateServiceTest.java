package com.ordertogether.paymentservice.payment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.domain.vo.Price;
import com.ordertogether.paymentservice.payment.repository.PaymentRepository;
import com.ordertogether.paymentservice.support.fixture.PaymentEventFixtureBuilder;
import com.ordertogether.paymentservice.support.fixture.PaymentOrderFixtureBuilder;
import com.ordertogether.paymentservice.exception.InvalidPaymentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("결제 검증 서비스 테스트")
class PaymentValidateServiceTest {

    @InjectMocks
    private PaymentValidateService paymentValidateService;

    @Mock
    private PaymentRepository paymentRepository;

    @Nested
    @DisplayName("결제 금액 검증 시")
    class WhenValidatingAmount {

        @Test
        @DisplayName("금액이 일치하지 않으면 예외를 발생시킨다")
        void thenThrowException() {
            //given
            OrderId orderId = OrderId.from("test-order");
            Long requestedAmount = 5000L;

            PaymentEvent paymentEvent = new PaymentEventFixtureBuilder()
                .addPaymentOrder(
                    new PaymentOrderFixtureBuilder()
                        .withAmount(Price.valueOf(1000))
                        .build())
                .addPaymentOrder(
                    new PaymentOrderFixtureBuilder()
                        .withAmount(Price.valueOf(2000))
                        .build())
                .build();

            given(paymentRepository.selectPaymentEvent(any()))
                .willReturn(paymentEvent);

            // when & then
            assertThatThrownBy(() -> paymentValidateService.validateAmount(orderId, requestedAmount))
                .isInstanceOf(InvalidPaymentException.class);
        }
    }

}
