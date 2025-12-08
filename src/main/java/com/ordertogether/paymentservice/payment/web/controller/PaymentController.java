package com.ordertogether.paymentservice.payment.web.controller;

import com.ordertogether.paymentservice.common.web.response.ApiResponse;
import com.ordertogether.paymentservice.payment.web.client.TossPaymentsWebClient;
import com.ordertogether.paymentservice.payment.web.request.TossPaymentsConfirmRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class PaymentController {

    private final TossPaymentsWebClient tossPaymentsWebClient;

    @PostMapping("/v1/toss/payment/confirm")
    public ResponseEntity<ApiResponse<String>> confirmPayment(@RequestBody TossPaymentsConfirmRequest request) {
        String data = tossPaymentsWebClient.confirmPayment(
            request.paymentKey(),
            request.orderId(),
            request.amount()
        );

        log.info("Payment confirmation response: {}", data);
        return ResponseEntity.ok(ApiResponse.with(HttpStatus.OK, "결제 승인을 완료하였습니다.", data));
    }

}


