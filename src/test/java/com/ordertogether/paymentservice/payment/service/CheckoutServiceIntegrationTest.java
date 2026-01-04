package com.ordertogether.paymentservice.payment.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentOrder;
import com.ordertogether.paymentservice.payment.repository.PaymentEventJPARepository;
import com.ordertogether.paymentservice.payment.service.command.CheckoutCommand;
import com.ordertogether.paymentservice.payment.service.result.CheckoutResult;
import com.ordertogether.paymentservice.payment.web.client.ProductClient;
import com.ordertogether.paymentservice.support.fixture.ProductFixtureBuilder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("체크아웃 서비스 통합 테스트")
@ActiveProfiles(profiles = "test")
@SpringBootTest
class CheckoutServiceIntegrationTest {

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private PaymentEventJPARepository paymentEventJPARepository;

    @MockitoBean
    private ProductClient productClient;

    @DisplayName("유효한 결제 요청 시")
    @Nested
    class WhenValidRequest {
        @Test
        @Transactional
        @DisplayName("결제 이벤트와 주문을 저장한다")
        void thenSavePaymentEventAndPaymentOrder() {
            //given
            String orderId = UUID.randomUUID().toString();
            CheckoutCommand checkoutCommand = CheckoutCommand.builder()
                .buyerId(1L)
                .productIds(List.of(1L, 2L))
                .idempotencyKey(orderId)
                .build();

            given(productClient.getProducts(List.of(1L, 2L)))
                .willReturn(List.of(
                    new ProductFixtureBuilder()
                        .withId(1L)
                        .withName("test_product_001")
                        .withPrice(BigDecimal.valueOf(10000))
                        .build(),
                    new ProductFixtureBuilder()
                        .withId(2L)
                        .withName("test_product_002")
                        .withPrice(BigDecimal.valueOf(20000))
                        .build()
                ));

            //when
            CheckoutResult checkoutResult = checkoutService.checkout(checkoutCommand);

            //then
            assertAll(
                () -> assertThat(checkoutResult.orderId()).isEqualTo(orderId),
                () -> assertThat(checkoutResult.totalAmount()).isEqualTo(30000L),
                () -> assertThat(checkoutResult.orderName()).isEqualTo("test_product_001, test_product_002")
            );

            //PaymentEvent 검증
            Optional<PaymentEvent> optionalPaymentEvent = paymentEventJPARepository.findByOrderId(orderId);
            assertThat(optionalPaymentEvent).isPresent();

            PaymentEvent paymentEvent = optionalPaymentEvent.get();
            assertAll(
                () -> assertThat(paymentEvent.getBuyerId()).isEqualTo(1L),
                () -> assertThat(paymentEvent.getOrderId()).isEqualTo(orderId),
                () -> assertThat(paymentEvent.isPaymentDone()).isFalse(),
                () -> assertThat(paymentEvent.getApprovedAt()).isNull(),
                () -> assertThat(paymentEvent.getOrderName()).isEqualTo("test_product_001, test_product_002")
            );

            //PaymentOrder 검증
            List<PaymentOrder> paymentOrders = paymentEvent.getPaymentOrders();
            assertAll(
                () -> assertThat(paymentOrders).hasSize(2),
                () -> assertThat(paymentOrders).allSatisfy(it -> assertTrue(it.getPaymentStatus().isNotStarted())),
                () -> assertThat(paymentOrders).filteredOn(it -> it.getProductId().equals(1L))
                    .singleElement()
                    .satisfies(paymentOrder -> assertThat(paymentOrder.getAmount()).isEqualTo(BigDecimal.valueOf(10000))),
                () -> assertThat(paymentOrders).filteredOn(it -> it.getProductId().equals(2L))
                    .singleElement()
                    .satisfies(paymentOrder -> assertThat(paymentOrder.getAmount()).isEqualTo(BigDecimal.valueOf(20000)))
            );
        }
    }
}
