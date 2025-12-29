package com.ordertogether.paymentservice.payment.domain;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Builder
@ToString
@Getter
@RequiredArgsConstructor
public class Product {

    private final Long id;
    private final String name;
    private final BigDecimal price;
    private final Integer quantity;
    private final Long sellerId;

}
