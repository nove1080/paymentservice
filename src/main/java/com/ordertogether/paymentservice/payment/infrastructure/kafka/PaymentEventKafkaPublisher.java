package com.ordertogether.paymentservice.payment.infrastructure.kafka;

import static com.ordertogether.paymentservice.payment.infrastructure.kafka.PaymentEventKafkaPublisher.Topic.PAYMENT_CONFIRM_SUCCESS;

import com.ordertogether.paymentservice.payment.domain.PaymentConfirmMessage;
import com.ordertogether.paymentservice.payment.service.PaymentEventPublisher;
import com.ordertogether.paymentservice.payment.service.PaymentOutboxService;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventKafkaPublisher implements PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PaymentOutboxService paymentOutboxService;

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishPaymentConfirmedEvent(PaymentConfirmMessage message) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(PAYMENT_CONFIRM_SUCCESS.getValue(), message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                paymentOutboxService.markAsPublished(message.orderId());
            } else {
                log.error("Failed to publish payment confirmed event for orderId: {}", message.orderId(), ex);
            }
        });
    }

    @RequiredArgsConstructor
    @Getter
    enum Topic {
        PAYMENT_CONFIRM_SUCCESS("payment-confirm-success");

        private final String value;
    }
}
