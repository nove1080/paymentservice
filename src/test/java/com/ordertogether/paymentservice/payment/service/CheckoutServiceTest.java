package com.ordertogether.paymentservice.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.Product;
import com.ordertogether.paymentservice.payment.repository.PaymentEventJPARepository;
import com.ordertogether.paymentservice.payment.service.command.CheckoutCommand;
import com.ordertogether.paymentservice.payment.service.result.CheckoutResult;
import com.ordertogether.paymentservice.payment.web.client.ProductClient;
import com.ordertogether.paymentservice.support.fixture.PaymentEventFixtureBuilder;
import com.ordertogether.paymentservice.support.fixture.PaymentOrderFixtureBuilder;
import com.ordertogether.paymentservice.support.fixture.ProductFixtureBuilder;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("체크아웃 서비스 단위 테스트")
@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @InjectMocks
    private CheckoutService checkoutService;

    @Mock
    private ProductClient productClient;

    @Mock
    private PaymentEventJPARepository paymentEventJPARepository;

    @DisplayName("유효한 결제 요청 시")
    @Nested
    class WhenValidRequest {
        @Test
        @DisplayName("결제 이벤트와 주문을 저장한다")
        void thenSavePaymentEventAndPaymentOrder() {
            //given
            CheckoutCommand checkoutCommand = CheckoutCommand.builder()
                .idempotencyKey("TEST-order-id-001")
                .buyerId(1L)
                .productIds(List.of(1L, 2L))
                .build();

            List<Product> products = List.of(
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
            );
            given(productClient.getProducts(List.of(1L, 2L)))
                .willReturn(products);

            PaymentEvent paymentEvent = new PaymentEventFixtureBuilder()
                .withOrderId("TEST-order-id-001")
                .addPaymentOrder(new PaymentOrderFixtureBuilder()
                    .withProductName("test_product_001")
                    .withAmount(BigDecimal.valueOf(10000))
                    .build())
                .addPaymentOrder(new PaymentOrderFixtureBuilder()
                    .withProductName("test_product_002")
                    .withAmount(BigDecimal.valueOf(20000))
                    .build())
                .build();
            given(paymentEventJPARepository.save(any(PaymentEvent.class)))
                .willReturn(paymentEvent);

            //when
            CheckoutResult checkoutResult = checkoutService.checkout(checkoutCommand);

            //then
            then(paymentEventJPARepository).should().save(any(PaymentEvent.class));

            Assertions.assertAll(
                () -> assertThat(checkoutResult.orderId()).isEqualTo("TEST-order-id-001"),
                () -> assertThat(checkoutResult.orderName()).isEqualTo("test_product_001, test_product_002"),
                () -> assertThat(checkoutResult.totalAmount()).isEqualTo(30000L)
            );
        }
    }
}
