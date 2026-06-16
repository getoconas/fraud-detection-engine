package com.mago.infrastructure.configuration;

import com.mago.application.port.FraudAlertPublisher;
import com.mago.application.service.CustomerHistoryService;
import com.mago.application.service.ProcessTransactionUseCase;
import com.mago.domain.service.*;
import com.mago.infrastructure.adapter.output.ConsoleFraudAlertPublisher;
import com.mago.infrastructure.adapter.output.KafkaFraudAlertPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

/**
 * Configuración de Spring para el dominio.
 * <p>
 * Define los beans manualmente para mantener el dominio
 * libre de anotaciones de Spring.
 * <p>
 * TransactionRepository ya no se define aquí: el adaptador JPA
 * se registra automáticamente con @Component.
 */
@Configuration
public class DomainConfiguration {

    @Value("${app.kafka.topic.fraud-alerts}")
    private String fraudAlertsTopic;

    // --- Puertos (adaptadores de salida) ---

    @Bean
    FraudAlertPublisher fraudAlertPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        return new KafkaFraudAlertPublisher(kafkaTemplate, fraudAlertsTopic);
    }

    // --- Servicios de dominio ---

    @Bean
    FraudDetectionEngine fraudDetectionEngine() {
        List<FraudRule> rules = List.of(
                new HighAmountRule(),
                new ImpossibleTravelRule(),
                new VelocityRule(),
                new UnusualCountryRule()
        );
        return new FraudDetectionEngine(rules);
    }

    // --- Servicios de aplicación ---

    @Bean
    CustomerHistoryService customerHistoryService() {
        return new CustomerHistoryService();
    }

    @Bean
    ProcessTransactionUseCase processTransactionUseCase(
            com.mago.application.port.TransactionRepository transactionRepository,
            FraudAlertPublisher fraudAlertPublisher,
            FraudDetectionEngine fraudDetectionEngine,
            CustomerHistoryService customerHistoryService) {
        return new ProcessTransactionUseCase(
                transactionRepository,
                fraudAlertPublisher,
                fraudDetectionEngine,
                customerHistoryService
        );
    }
}