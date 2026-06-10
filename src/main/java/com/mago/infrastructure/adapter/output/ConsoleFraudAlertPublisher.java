package com.mago.infrastructure.adapter.output;

import com.mago.application.port.FraudAlertPublisher;
import com.mago.domain.model.FraudResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementación que publica alertas de fraude por consola.
 * <p>
 * Para desarrollo. Se reemplazará por Kafka.
 */
public class ConsoleFraudAlertPublisher implements FraudAlertPublisher {

    private static final Logger log = LoggerFactory.getLogger(ConsoleFraudAlertPublisher.class);

    @Override
    public void publish(List<FraudResult> frauds) {
        log.warn("🚨 FRAUD DETECTED! {} rule(s) triggered:", frauds.size());
        for (FraudResult fraud : frauds) {
            log.warn("  - [{}] {} (Card: {})",
                    fraud.ruleName(), fraud.reason(), fraud.cardNumber());
        }
    }
}