package com.mago.infrastructure.adapter.output;

import com.mago.application.port.FraudAlertPublisher;
import com.mago.domain.model.FraudResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

/**
 * Adaptador que publica alertas de fraude a un tópico de Kafka.
 * <p>
 * Reemplaza al {@link ConsoleFraudAlertPublisher} en producción.
 * Cada alerta se publica como un mensaje individual en el tópico configurado.
 */
public class KafkaFraudAlertPublisher implements FraudAlertPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaFraudAlertPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public KafkaFraudAlertPublisher(KafkaTemplate<String, Object> kafkaTemplate, String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(List<FraudResult> frauds) {
        for (FraudResult fraud : frauds) {
            kafkaTemplate.send(topic, fraud.cardNumber().value(), fraud)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Fraud alert published to topic {}: [{}] {}",
                                    topic, fraud.ruleName(), fraud.reason());
                        } else {
                            log.error("Failed to publish fraud alert to topic {}", topic, ex);
                        }
                    });
        }
    }
}