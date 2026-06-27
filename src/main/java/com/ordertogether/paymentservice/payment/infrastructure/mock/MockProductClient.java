package com.ordertogether.paymentservice.payment.infrastructure.mock;

import com.ordertogether.paymentservice.payment.domain.Product;
import com.ordertogether.paymentservice.payment.domain.vo.Price;
import com.ordertogether.paymentservice.payment.service.ProductClient;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"local", "test"})
public class MockProductClient implements ProductClient {

    private static final Map<Long, Long> PRODUCT_SELLER_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, Long> PRODUCT_PRICE_MAP = new ConcurrentHashMap<>();

    static {
        LongStream.rangeClosed(1, 100).forEach(id -> {
            // 판매자 ID: 1~5번=1번, 6~10번=2번, 11~15번=3번 판매자로 항상 고정
            long sellerId = ((id - 1) / 5) + 1;
            PRODUCT_SELLER_MAP.put(id, sellerId);

            long price = id * 1000L;
            PRODUCT_PRICE_MAP.put(id, price);
        });
    }

    @Override
    public List<Product> getProducts(List<Long> productIds) {
        return productIds.stream()
            .map(id -> {
                long safeId = (id < 1 || id > 100) ? 1L : id;

                return Product.builder()
                    .id(safeId)
                    .name("product_ID_%d".formatted(safeId))
                    .price(Price.valueOf(PRODUCT_PRICE_MAP.get(safeId)))
                    .quantity(2)
                    .sellerId(PRODUCT_SELLER_MAP.get(safeId))
                    .build();
            })
            .collect(Collectors.toList());
    }
}
