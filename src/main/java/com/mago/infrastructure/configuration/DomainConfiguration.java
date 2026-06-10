package com.mago.infrastructure.configuration;

import com.mago.application.port.FraudAlertPublisher;
import com.mago.application.port.TransactionRepository;
import com.mago.application.service.CustomerHistoryService;
import com.mago.application.service.ProcessTransactionUseCase;
import com.mago.domain.service.*;
import com.mago.infrastructure.adapter.output.ConsoleFraudAlertPublisher;
import com.mago.infrastructure.adapter.output.InMemoryTransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de Spring para el dominio.
 * <p>
 * Define los beans manualmente para mantener el dominio
 * libre de anotaciones de Spring.
 */
@Configuration
public class DomainConfiguration {

    // --- Puertos (adaptadores de salida) ---

    @Bean
    TransactionRepository transactionRepository() {
        return new InMemoryTransactionRepository();
    }

    @Bean
    FraudAlertPublisher fraudAlertPublisher() {
        return new ConsoleFraudAlertPublisher();
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
            TransactionRepository transactionRepository,
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