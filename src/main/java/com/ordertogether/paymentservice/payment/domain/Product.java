package com.ordertogether.paymentservice.payment.domain;

import com.ordertogether.paymentservice.payment.domain.vo.Price;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public record Product(
    Long id,
    String name,
    Price price,
    Integer quantity,
    Long sellerId
) {

}
