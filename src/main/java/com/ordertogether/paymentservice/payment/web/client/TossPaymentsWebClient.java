package com.ordertogether.paymentservice.payment.web.client;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class TossPaymentsWebClient {

    private final RestClient tossPaymentsRestClient;

    private static final String CONFIRM_PAYMENT_URL = "/v1/payments/confirm";

    public String confirmPayment(String paymentKey, String orderId, Long amount) {
        PaymentConfirmRequestBody body = PaymentConfirmRequestBody.builder()
            .paymentKey(paymentKey)
            .orderId(orderId)
            .amount(amount)
            .build();

        return tossPaymentsRestClient.post()
            .uri(CONFIRM_PAYMENT_URL)
            .body(body)
            .exchange((req, res) -> res.bodyTo(String.class));
    }

    @Builder
    record PaymentConfirmRequestBody(
        String paymentKey,
        String orderId,
        Long amount
    ) {

    }

}
