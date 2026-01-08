package com.ordertogether.paymentservice.payment.domain;

import com.ordertogether.paymentservice.payment.domain.vo.Price;
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
    private final Price price;
    private final Integer quantity;
    private final Long sellerId;

}
