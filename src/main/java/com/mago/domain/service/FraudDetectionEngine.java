package com.mago.domain.service;

import com.mago.domain.model.CustomerHistory;
import com.mago.domain.model.FraudResult;
import com.mago.domain.model.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Orquestador central de detección de fraudes.
 * <p>
 * Ejecuta todas las reglas de fraude contra una transacción
 * y recolecta los resultados. Es el punto de entrada del dominio
 * para evaluar si una transacción es fraudulenta.
 * <p>
 * Características:
 * <ul>
 *   <li>Ejecuta múltiples reglas en secuencia</li>
 *   <li>Una transacción puede disparar cero, una o varias reglas</li>
 *   <li>No depende de infraestructura (Spring, Kafka, etc.)</li>
 *   <li>Recibe las reglas por constructor (Inyección de Dependencias manual)</li>
 * </ul>
 * <p>
 * Ejemplo de uso:
 * <pre>{@code
 *   List<FraudRule> rules = List.of(
 *       new HighAmountRule(),
 *       new ImpossibleTravelRule()
 *   );
 *   FraudDetectionEngine engine = new FraudDetectionEngine(rules);
 *   List<FraudResult> frauds = engine.detect(transaction, history);
 * }</pre>
 *
 * @author mago
 */
public class FraudDetectionEngine {

    private final List<FraudRule> rules;

    /**
     * Crea el motor de detección con una lista de reglas.
     *
     * @param rules lista de reglas de fraude a aplicar
     * @throws IllegalArgumentException si la lista es nula o vacía
     */
    public FraudDetectionEngine(List<FraudRule> rules) {
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("Rules list cannot be null or empty");
        }
        this.rules = List.copyOf(rules); // copia defensiva inmutable
    }

    /**
     * Evalúa una transacción contra todas las reglas configuradas.
     * <p>
     * Una misma transacción puede activar múltiples reglas.
     * Por ejemplo: un monto muy alto en un país nuevo dispara
     * {@code HIGH_AMOUNT} y {@code UNUSUAL_COUNTRY}.
     *
     * @param transaction la transacción a evaluar
     * @param history     el historial del cliente (puede ser vacío)
     * @return lista de resultados de fraude. Vacía si la transacción es legítima.
     */
    public List<FraudResult> detect(Transaction transaction, CustomerHistory history) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        if (history == null) {
            throw new IllegalArgumentException("History cannot be null");
        }

        List<FraudResult> results = new ArrayList<>();

        for (FraudRule rule : rules) {
            rule.evaluate(transaction, history)
                    .ifPresent(results::add);
        }

        return Collections.unmodifiableList(results);
    }

    /**
     * Verifica si una transacción es fraudulenta según al menos una regla.
     *
     * @param transaction la transacción a evaluar
     * @param history     el historial del cliente
     * @return true si al menos una regla detecta fraude
     */
    public boolean isFraudulent(Transaction transaction, CustomerHistory history) {
        return !detect(transaction, history).isEmpty();
    }

    /**
     * @return copia inmutable de las reglas configuradas
     */
    public List<FraudRule> rules() {
        return rules;
    }
}