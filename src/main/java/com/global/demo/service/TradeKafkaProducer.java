package com.global.demo.service;

import com.global.demo.model.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TradeKafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(TradeKafkaProducer.class);

    private final KafkaTemplate<String, Trade> kafkaTemplate;
    private final String topicName;

    public TradeKafkaProducer(KafkaTemplate<String, Trade> kafkaTemplate,
                              @Value("${kafka.topic.confirmed-trades}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendConfirmedTrade(Trade trade) {
        log.info("Sending confirmed trade to Kafka: {}", trade.getTradeId());

        CompletableFuture<SendResult<String, Trade>> future = kafkaTemplate.send(topicName, trade.getTradeId(), trade);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent trade {} to topic {} with offset {}",
                        trade.getTradeId(), topicName, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send trade {} to topic {}", trade.getTradeId(), topicName, ex);
            }
        });
    }
}
