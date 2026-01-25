package com.ordertogether.paymentservice.payment.service;

import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.service.command.PGConfirmCommand;
import com.ordertogether.paymentservice.payment.service.command.PaymentConfirmCommand;
import com.ordertogether.paymentservice.payment.service.command.PaymentStatusUpdateCommand;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult;
import com.ordertogether.paymentservice.payment.service.result.PaymentConfirmResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentConfirmService {

    private final PaymentStatusUpdateService paymentStatusUpdateService;
    private final PaymentValidateService paymentValidateService;
    private final PaymentGatewayClient paymentGatewayClient;

    @Transactional
    public PaymentConfirmResult confirm(PaymentConfirmCommand command) {
        paymentStatusUpdateService.updatePaymentStatus(
            PaymentStatusUpdateCommand.builder()
                .paymentKey(command.paymentKey())
                .orderId(command.orderId())
                .status(PaymentStatus.EXECUTING)
                .build());
        paymentValidateService.validateAmount(command.orderId(), command.amount());
        PGConfirmResult pgConfirmResult = paymentGatewayClient.confirmPayment(PGConfirmCommand.builder()
            .paymentKey(command.paymentKey())
            .orderId(command.orderId())
            .amount(command.amount())
            .build());
        paymentStatusUpdateService.updatePaymentStatus(
            PaymentStatusUpdateCommand.builder()
                .orderId(command.orderId())
                .status(pgConfirmResult.status())
                .successExtraInfo(pgConfirmResult.successExtraInfo())
                .failureExtraInfo(pgConfirmResult.failureExtraInfo())
                .build());

        return PaymentConfirmResult.builder()
            .paymentStatus(pgConfirmResult.status())
            .failure(pgConfirmResult.failureExtraInfo())
            .build();
    }

}
