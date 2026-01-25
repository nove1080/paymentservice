package com.ordertogether.paymentservice.payment.service;

import com.ordertogether.paymentservice.payment.service.command.PGConfirmCommand;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult;

public interface PaymentGatewayClient {

    PGConfirmResult confirmPayment(PGConfirmCommand request);

}
