package com.ordertogether.paymentservice.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.infrastructure.jpa.PaymentRepositoryJPAAdapter;
import com.ordertogether.paymentservice.support.fixture.PaymentEventFixtureBuilder;
import com.ordertogether.paymentservice.support.fixture.PaymentOrderFixtureBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest(showSql = false)
@Import(PaymentRepositoryJPAAdapter.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles(profiles = "test")
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("주문식별자로 결제 이벤트를 조회할 수 있다")
    void givenOrderId_whenSelectPaymentEvent_thenSuccess() {
        //given
        OrderId orderId = OrderId.valueOf("test-order-id");

        PaymentEvent event = new PaymentEventFixtureBuilder()
            .withOrderId(orderId)
            .addPaymentOrder(new PaymentOrderFixtureBuilder()
                .build())
            .addPaymentOrder(new PaymentOrderFixtureBuilder()
                .build())
            .build();

        paymentRepository.insertPaymentEvent(event);

        //when
        PaymentEvent paymentEvent = paymentRepository.selectPaymentEvent(orderId);

        //then
        assertThat(paymentEvent.getOrderId()).isEqualTo(orderId);
        assertThat(paymentEvent.getPaymentOrders()).hasSize(2);
    }
}
