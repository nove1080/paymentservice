package com.ordertogether.paymentservice.payment.infrastructure.mock;

import com.ordertogether.paymentservice.payment.domain.Product;
import com.ordertogether.paymentservice.payment.domain.vo.Price;
import com.ordertogether.paymentservice.payment.service.ProductClient;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"local", "test"})
public class MockProductClient implements ProductClient {

    private static final Map<Long, Long> PRODUCT_SELLER_MAP = Map.of(
        1L, 1L,
        2L, 1L,
        3L, 2L,
        4L, 2L
    );

    @Override
    public List<Product> getProducts(List<Long> productIds) {
        return productIds.stream()
            .map(id ->
                Product.builder()
                    .id(id)
                    .name("test_product_%s".formatted(id))
                    .price(Price.valueOf(id * 1000))
                    .quantity(2)
                    .sellerId(PRODUCT_SELLER_MAP.getOrDefault(id, 1L))
                    .build()
            ).collect(Collectors.toList());
    }
}
