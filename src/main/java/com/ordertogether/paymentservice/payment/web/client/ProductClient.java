package com.ordertogether.paymentservice.payment.web.client;

import com.ordertogether.paymentservice.payment.domain.Product;
import java.util.List;

public interface ProductClient {

    List<Product> getProducts(List<Long> productIds);

}
