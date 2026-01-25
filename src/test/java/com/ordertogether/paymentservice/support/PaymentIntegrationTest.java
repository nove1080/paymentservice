package com.ordertogether.paymentservice.support;

import com.ordertogether.paymentservice.config.PaymentTestConfig;
import com.ordertogether.paymentservice.support.database.PaymentDatabaseHelper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(PaymentTestConfig.class)
public abstract class PaymentIntegrationTest {

    @Autowired
    private PaymentDatabaseHelper paymentDatabaseHelper;

    @BeforeEach
    void setUp() {
        paymentDatabaseHelper.clean();
    }

}
