package com.ordertogether.paymentservice.common.config;

import com.ordertogether.paymentservice.payment.domain.LedgerEventMessage;
import com.ordertogether.paymentservice.payment.domain.WalletEventMessage;
import com.ordertogether.paymentservice.payment.domain.WalletEventMessage.Topic;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.util.backoff.FixedBackOff;

@EnableKafka
@RequiredArgsConstructor
@Configuration
public class KafkaConsumerConfiguration {

    private final KafkaProperties kafkaProperties;

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, LedgerEventMessage>> ledgerUpdatedKafkaListenerContainerFactory(
        KafkaTemplate<String, Object> kafkaTemplate,
        KafkaTransactionManager<String, Object> kafkaTransactionManager) {
        ConcurrentKafkaListenerContainerFactory<String, LedgerEventMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(ledgerUpdatedConsumerFactory());
        factory.getContainerProperties().setKafkaAwareTransactionManager(kafkaTransactionManager);

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
            (record, exception) -> {
                String topic = LedgerEventMessage.Topic.LEDGER_RECORD_SUCCESS_DLT;
                return new TopicPartition(topic, record.partition());
            });

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, LedgerEventMessage> ledgerUpdatedConsumerFactory() {
        Map<String, Object> props = consumerConfigs();
        props.put(JacksonJsonDeserializer.TYPE_MAPPINGS, "LedgerEventMessage:com.ordertogether.paymentservice.payment.domain.LedgerEventMessage");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, WalletEventMessage>> walletUpdatedKafkaListenerContainerFactory(
        KafkaTemplate<String, Object> kafkaTemplate,
        KafkaTransactionManager<String, Object> kafkaTransactionManager) {
        ConcurrentKafkaListenerContainerFactory<String, WalletEventMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(walletUpdatedConsumerFactory());
        factory.getContainerProperties().setKafkaAwareTransactionManager(kafkaTransactionManager);

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
            (record, exception) -> {
                String topic = Topic.SETTLEMENT_SUCCESS_DLT;
                return new TopicPartition(topic, record.partition());
            });

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, WalletEventMessage> walletUpdatedConsumerFactory() {
        Map<String, Object> props = consumerConfigs();
        props.put(JacksonJsonDeserializer.TYPE_MAPPINGS, "WalletEventMessage:com.ordertogether.paymentservice.payment.domain.WalletEventMessage");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getConsumer().getAutoOffsetReset());
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, kafkaProperties.getConsumer().getKeyDeserializer());
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, kafkaProperties.getConsumer().getValueDeserializer());
        return props;
    }

}
