package com.ordertogether.paymentservice.payment.web.client;

import com.ordertogether.paymentservice.payment.domain.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MockProductClient implements ProductClient {

    @Override
    public List<Product> getProducts(List<Long> productIds) {
        return productIds.stream()
            .map(id ->
                Product.builder()
                    .id(id)
                    .name("test_product_%s".formatted(id))
                    .price(BigDecimal.valueOf(id * 10000))
                    .quantity(2)
                    .sellerId(1L)
                    .build()
            ).collect(Collectors.toList());
    }
}
