package com.mago.domain.service;

import com.mago.domain.model.CustomerHistory;
import com.mago.domain.model.FraudResult;
import com.mago.domain.model.Money;
import com.mago.domain.model.Transaction;

import java.util.Optional;

/**
 * Regla de fraude por monto anormalmente alto.
 * <p>
 * Detecta cuando el monto de una transacción es 5 veces mayor
 * que el promedio de los últimos 30 días del cliente.
 * <p>
 * No aplica si el cliente no tiene historial previo (promedio = 0).
 *
 * @author mago
 */
public class HighAmountRule implements FraudRule {

    private static final int MULTIPLIER_THRESHOLD = 5;

    @Override
    public Optional<FraudResult> evaluate(Transaction transaction, CustomerHistory history) {
        Money average = history.averageAmount30Days();

        // Si no hay historial, no podemos comparar
        if (average.equals(Money.zero())) {
            return Optional.empty();
        }

        Money threshold = average.multiply(MULTIPLIER_THRESHOLD);

        if (transaction.amount().isGreaterThan(threshold)) {
            return Optional.of(FraudResult.of(
                    "HIGH_AMOUNT",
                    String.format("Amount %s is more than %dx the 30-day average of %s",
                            transaction.amount(), MULTIPLIER_THRESHOLD, average),
                    transaction
            ));
        }

        return Optional.empty();
    }
}