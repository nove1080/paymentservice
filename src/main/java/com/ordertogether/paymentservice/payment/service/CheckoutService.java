package com.ordertogether.paymentservice.payment.service;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentOrder;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.domain.Product;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.repository.PaymentRepository;
import com.ordertogether.paymentservice.payment.service.command.CheckoutCommand;
import com.ordertogether.paymentservice.payment.service.result.CheckoutResult;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CheckoutService {

    private final ProductClient productClient;
    private final PaymentRepository paymentRepository;

    @Transactional
    public CheckoutResult checkout(CheckoutCommand command) {
        List<Product> products = getProducts(command);
        PaymentEvent paymentEvent = savePaymentEvent(command, products);

        return CheckoutResult.builder()
            .orderId(paymentEvent.getOrderId())
            .orderName(paymentEvent.getOrderName())
            .totalAmount(paymentEvent.totalAmount())
            .build();
    }

    private List<Product> getProducts(CheckoutCommand command) {
        List<Product> products = productClient.getProducts(command.productIds());

        if (hasMissingProducts(command.productIds(), products)) {
            throw new IllegalArgumentException("일부 상품 정보가 누락되었습니다. [요청된 상품 ID: %s, 조회된 상품 ID: %s]"
                .formatted(
                    command.productIds().toString(),
                    products.stream()
                        .map(Product::id)
                        .toList().toString()
                ));
        }

        return products;
    }

    private static boolean hasMissingProducts(List<Long> requestedProductIds, List<Product> products) {
        return products.size() != requestedProductIds.size();
    }

    private PaymentEvent savePaymentEvent(CheckoutCommand command, List<Product> products) {
        PaymentEvent paymentEvent = PaymentEvent.builder()
            .orderId(new OrderId(command.idempotencyKey()))
            .buyerId(command.buyerId())
            .orderName(products.stream()
                .map(Product::name)
                .collect(Collectors.joining(", ")))
            .isPaymentDone(false)
            .build();

        products.stream()
            .map(it -> PaymentOrder.builder()
                .productId(it.id())
                .productName(it.name())
                .sellerId(it.sellerId())
                .amount(it.price())
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .build()
            ).forEach(paymentEvent::addPaymentOrder);

        paymentRepository.insertPaymentEvent(paymentEvent);
        return paymentEvent;
    }
}
