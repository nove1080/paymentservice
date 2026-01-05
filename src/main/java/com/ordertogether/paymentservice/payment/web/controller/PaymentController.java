package com.ordertogether.paymentservice.payment.web.controller;

import com.ordertogether.paymentservice.common.util.IdempotencyKeyGenerator;
import com.ordertogether.paymentservice.common.web.response.ApiResponse;
import com.ordertogether.paymentservice.payment.service.CheckoutService;
import com.ordertogether.paymentservice.payment.service.command.CheckoutCommand;
import com.ordertogether.paymentservice.payment.web.client.TossPaymentsWebClient;
import com.ordertogether.paymentservice.payment.web.request.CheckoutRequest;
import com.ordertogether.paymentservice.payment.web.request.TossPaymentsConfirmRequest;
import com.ordertogether.paymentservice.payment.web.response.CheckoutResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    private final CheckoutService checkoutService;

    @PostMapping("/v1/payment/checkout")
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(@RequestBody @Validated CheckoutRequest request) {
        CheckoutCommand checkoutCommand = CheckoutCommand.builder()
            .buyerId(request.buyerId())
            .idempotencyKey(IdempotencyKeyGenerator.generate(request.seed()))
            .productIds(request.productIds())
            .build();

        CheckoutResponse response = CheckoutResponse.from(checkoutService.checkout(checkoutCommand));
        return ResponseEntity.ok(ApiResponse.with(HttpStatus.OK, "결제 준비가 완료되었습니다.", response));
    }

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


