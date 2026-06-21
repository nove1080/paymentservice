package com.ordertogether.paymentservice.payment.infrastructure.kafka;

import com.ordertogether.paymentservice.mail.service.PaymentEmailSender;
import com.ordertogether.paymentservice.payment.domain.LedgerEventMessage;
import com.ordertogether.paymentservice.payment.domain.WalletEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentCompleteDltKafkaConsumer {

    private final PaymentEmailSender emailSender;

    @KafkaListener(
        topics = {WalletEventMessage.Topic.SETTLEMENT_SUCCESS_DLT, LedgerEventMessage.Topic.LEDGER_RECORD_SUCCESS_DLT},
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeSettlementSuccessMessage(
        @Payload String rawMessage,
        @Header(value = KafkaHeaders.DLT_EXCEPTION_MESSAGE, required = false) String exceptionMessage,
        @Header(value = KafkaHeaders.DLT_ORIGINAL_TOPIC, required = false) String originalTopic
    ) {
        log.warn("[DLT Alert] Topic: {}, Exception: {}", originalTopic, exceptionMessage);
        emailSender.sendDltAlertEmail(originalTopic, rawMessage, exceptionMessage);
    }

}
