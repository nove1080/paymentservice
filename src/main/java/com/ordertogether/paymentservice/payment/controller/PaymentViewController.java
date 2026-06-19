package com.ordertogether.paymentservice.payment.controller;

import com.ordertogether.paymentservice.common.util.IdempotencyKeyGenerator;
import com.ordertogether.paymentservice.payment.service.CheckoutService;
import com.ordertogether.paymentservice.payment.service.command.CheckoutCommand;
import com.ordertogether.paymentservice.payment.service.result.CheckoutResult;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PaymentViewController {

    private final CheckoutService checkoutService;

    @GetMapping("/")
    public String preparePayment(Model model) {
        CheckoutResult checkoutResult = checkoutService.checkout(CheckoutCommand.builder()
            .idempotencyKey(IdempotencyKeyGenerator.generate(LocalDateTime.now().toString()))
            .buyerId(1L)
            .productIds(List.of(1L, 2L, 3L, 4L))
            .build());

        model.addAttribute("orderId", checkoutResult.orderId().value());
        model.addAttribute("orderName", checkoutResult.orderName());
        model.addAttribute("amount", checkoutResult.totalAmount());
        return "payment/checkout";
    }

    @GetMapping("/payment/success")
    public String successPage() {
        return "payment/success";
    }

    @GetMapping("/fail")
    public String failPage() {
        return "fail";
    }
}
