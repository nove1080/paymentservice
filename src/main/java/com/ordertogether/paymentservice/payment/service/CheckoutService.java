package com.ordertogether.paymentservice.payment.service;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentOrder;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.Product;
import com.ordertogether.paymentservice.payment.repository.PaymentEventJPARepository;
import com.ordertogether.paymentservice.payment.service.command.CheckoutCommand;
import com.ordertogether.paymentservice.payment.service.result.CheckoutResult;
import com.ordertogether.paymentservice.payment.web.client.ProductClient;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CheckoutService {

    private final ProductClient productClient;
    private final PaymentEventJPARepository paymentEventRepository;

    @Transactional
    public CheckoutResult checkout(CheckoutCommand command) {
        List<Product> products = productClient.getProducts(command.productIds());
        PaymentEvent paymentEvent = savePaymentEvent(command, products);

        return CheckoutResult.builder()
            .orderId(paymentEvent.getOrderId())
            .orderName(paymentEvent.getOrderName())
            .totalAmount(paymentEvent.totalAmount())
            .build();
    }

    private PaymentEvent savePaymentEvent(CheckoutCommand command, List<Product> products) {
        PaymentEvent paymentEvent = PaymentEvent.builder()
            .orderId(command.idempotencyKey())
            .buyerId(command.buyerId())
            .orderName(products.stream()
                .map(Product::getName)
                .collect(Collectors.joining(", ")))
            .isPaymentDone(false)
            .build();

        products.stream()
            .map(it -> PaymentOrder.builder()
                .productId(it.getId())
                .productName(it.getName())
                .sellerId(it.getSellerId())
                .amount(it.getPrice())
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .build()
            ).forEach(paymentEvent::addPaymentOrder);

        return paymentEventRepository.save(paymentEvent);
    }
}
