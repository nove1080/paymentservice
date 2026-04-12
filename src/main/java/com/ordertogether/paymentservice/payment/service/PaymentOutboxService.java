package com.ordertogether.paymentservice.payment.service;

import com.ordertogether.paymentservice.common.util.PartitionKeyGenerator;
import com.ordertogether.paymentservice.exception.PaymentException;
import com.ordertogether.paymentservice.payment.domain.EventType;
import com.ordertogether.paymentservice.payment.domain.PaymentConfirmMessage;
import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentOutbox;
import com.ordertogether.paymentservice.payment.repository.PaymentRepository;
import com.ordertogether.paymentservice.payment.service.command.PaymentStatusUpdateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
public class PaymentOutboxService {

    private final JsonMapper objectMapper;
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentConfirmMessage insertPaymentOutbox(PaymentStatusUpdateCommand command) {
        if (command.isNotSuccess()) {
            throw new PaymentException("결제에 성공하지 못하였습니다. [orderId = %s]".formatted(command.orderId()));
        }

        PaymentEvent paymentEvent = paymentRepository.selectPaymentEvent(command.orderId());
        PaymentConfirmMessage paymentMessage = PaymentConfirmMessage.from(paymentEvent);
        String payload = objectMapper.writeValueAsString(paymentMessage);

        PaymentOutbox paymentOutbox = PaymentOutbox.builder()
            .idempotencyKey(command.orderId().value())
            .eventType(EventType.PAYMENT_CONFIRM_SUCCESS)
            .partitionKey(PartitionKeyGenerator.generate())
            .payload(payload)
            .published(false)
            .build();

        paymentRepository.insertPaymentOutbox(paymentOutbox);
        return paymentMessage;
    }

}
