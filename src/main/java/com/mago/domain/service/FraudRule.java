package com.mago.domain.service;

import com.mago.domain.model.CustomerHistory;
import com.mago.domain.model.FraudResult;
import com.mago.domain.model.Transaction;

import java.util.Optional;

/**
 * Interfaz que define una regla de detección de fraude.
 * <p>
 * Cada implementación evalúa un tipo específico de fraude.
 * Sigue el principio Open/Closed: nuevas reglas se agregan
 * como nuevas implementaciones, sin modificar las existentes.
 * <p>
 * El resultado es un {@link FraudResult} si se detecta fraude,
 * o {@link Optional#empty()} si la transacción es legítima.
 *
 * @author mago
 */
@FunctionalInterface
public interface FraudRule {

    /**
     * Evalúa si una transacción es fraudulenta según esta regla.
     *
     * @param transaction la transacción a evaluar
     * @param history     el historial del cliente al momento de la transacción
     * @return {@link FraudResult} si se detecta fraude, {@link Optional#empty()} si no
     */
    Optional<FraudResult> evaluate(Transaction transaction, CustomerHistory history);
}