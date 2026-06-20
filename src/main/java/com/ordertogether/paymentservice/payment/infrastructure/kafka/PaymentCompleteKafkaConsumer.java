package com.ordertogether.paymentservice.payment.infrastructure.kafka;

import com.ordertogether.paymentservice.payment.domain.LedgerEventMessage;
import com.ordertogether.paymentservice.payment.domain.WalletEventMessage;
import com.ordertogether.paymentservice.payment.service.PaymentCompleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentCompleteKafkaConsumer {

    private final PaymentCompleteService paymentCompleteService;

    @KafkaListener(
        topics = WalletEventMessage.Topic.SETTLEMENT_SUCCESS,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "walletUpdatedKafkaListenerContainerFactory"
    )
    public void consumePaymentConfirmMessage(WalletEventMessage message) {
        log.info("Received topic: {}, message: {}", WalletEventMessage.Topic.SETTLEMENT_SUCCESS, message);
        paymentCompleteService.completePayment(message);
    }
    
    @KafkaListener(
        topics = LedgerEventMessage.Topic.LEDGER_RECORD_SUCCESS,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "ledgerUpdatedKafkaListenerContainerFactory"
    )
    public void consumePaymentConfirmMessage(LedgerEventMessage message) {
        log.info("Received topic: {}, message: {}", LedgerEventMessage.Topic.LEDGER_RECORD_SUCCESS, message);
        paymentCompleteService.completePayment(message);
    }

}
