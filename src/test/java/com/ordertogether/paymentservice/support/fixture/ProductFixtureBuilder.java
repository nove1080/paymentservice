package com.ordertogether.paymentservice.support.fixture;

import com.ordertogether.paymentservice.payment.domain.Product;
import com.ordertogether.paymentservice.payment.domain.vo.Price;
import java.math.BigDecimal;

public class ProductFixtureBuilder {

    private Long id = 1L;
    private String name = "test_product_001";
    private BigDecimal price = BigDecimal.valueOf(10000);
    private Integer quantity = 1;
    private Long sellerId = 1L;

    public ProductFixtureBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ProductFixtureBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductFixtureBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public Product build() {
        return Product.builder()
            .id(id)
            .name(name)
            .price(new Price(price))
            .quantity(quantity)
            .sellerId(sellerId)
            .build();
    }

}
