package com.ordertogether.paymentservice.payment.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PaymentViewController {

    @GetMapping("/")
    public String preparePayment(Model model) {
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
