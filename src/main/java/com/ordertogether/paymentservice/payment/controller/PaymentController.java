package com.ordertogether.paymentservice.payment.controller;

import com.ordertogether.paymentservice.common.util.IdempotencyKeyGenerator;
import com.ordertogether.paymentservice.common.web.response.ApiResponse;
import com.ordertogether.paymentservice.payment.controller.request.CheckoutRequest;
import com.ordertogether.paymentservice.payment.controller.request.PaymentConfirmRequest;
import com.ordertogether.paymentservice.payment.controller.response.CheckoutResponse;
import com.ordertogether.paymentservice.payment.controller.response.PaymentConfirmResponse;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.service.CheckoutService;
import com.ordertogether.paymentservice.payment.service.PaymentConfirmService;
import com.ordertogether.paymentservice.payment.service.command.CheckoutCommand;
import com.ordertogether.paymentservice.payment.service.command.PaymentConfirmCommand;
import com.ordertogether.paymentservice.payment.service.result.PaymentConfirmResult;
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

    private final CheckoutService checkoutService;
    private final PaymentConfirmService paymentConfirmService;

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

    @PostMapping("/v1/payment/confirm")
    public ResponseEntity<ApiResponse<PaymentConfirmResponse>> confirmPayment(@RequestBody @Validated PaymentConfirmRequest request) {
        PaymentConfirmCommand confirmCommand = PaymentConfirmCommand.builder()
            .paymentKey(request.paymentKey())
            .orderId(new OrderId(request.orderId()))
            .amount(request.amount())
            .build();

        PaymentConfirmResult confirmResult = paymentConfirmService.confirm(confirmCommand);

        PaymentConfirmResponse responseData = PaymentConfirmResponse.from(confirmResult);
        return ResponseEntity.ok(ApiResponse.with(HttpStatus.OK, "결제 승인을 완료하였습니다.", responseData));
    }

}


