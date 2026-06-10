package com.mago.application.service;

import com.mago.application.port.FraudAlertPublisher;
import com.mago.application.port.TransactionRepository;
import com.mago.domain.model.CustomerHistory;
import com.mago.domain.model.FraudResult;
import com.mago.domain.model.Transaction;
import com.mago.domain.service.FraudDetectionEngine;

import java.util.List;

/**
 * Caso de uso principal: procesar una transacción entrante.
 * <p>
 * Orquesta el flujo completo:
 * <ol>
 *   <li>Guarda la transacción</li>
 *   <li>Recupera el historial del cliente</li>
 *   <li>Ejecuta el motor de detección de fraude</li>
 *   <li>Si hay fraudes, publica alertas</li>
 * </ol>
 * <p>
 * Depende de puertos (interfaces), no de implementaciones concretas.
 *
 * @author mago
 */
public class ProcessTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final FraudAlertPublisher fraudAlertPublisher;
    private final FraudDetectionEngine fraudDetectionEngine;
    private final CustomerHistoryService customerHistoryService;

    public ProcessTransactionUseCase(TransactionRepository transactionRepository,
                                     FraudAlertPublisher fraudAlertPublisher,
                                     FraudDetectionEngine fraudDetectionEngine,
                                     CustomerHistoryService customerHistoryService) {
        this.transactionRepository = transactionRepository;
        this.fraudAlertPublisher = fraudAlertPublisher;
        this.fraudDetectionEngine = fraudDetectionEngine;
        this.customerHistoryService = customerHistoryService;
    }

    /**
     * Procesa una transacción y devuelve los fraudes detectados.
     *
     * @param transaction la transacción a procesar
     * @return lista de fraudes detectados (vacía si es legítima)
     */
    public List<FraudResult> execute(Transaction transaction) {
        // 1. Guardar la transacción
        transactionRepository.save(transaction);

        // 2. Obtener historial del cliente
        CustomerHistory history = customerHistoryService.getHistory(transaction.cardNumber());

        // 3. Detectar fraudes
        List<FraudResult> frauds = fraudDetectionEngine.detect(transaction, history);

        // 4. Publicar alertas si hay fraudes
        if (!frauds.isEmpty()) {
            fraudAlertPublisher.publish(frauds);
        }

        // 5. Actualizar historial con esta transacción
        customerHistoryService.updateHistory(transaction, history);

        return frauds;
    }
}