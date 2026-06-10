package com.mago.domain.service;

import com.mago.domain.model.CustomerHistory;
import com.mago.domain.model.FraudResult;
import com.mago.domain.model.Transaction;

import java.util.Optional;

/**
 * Regla de fraude por velocidad excesiva de transacciones.
 * <p>
 * Detecta cuando un cliente realiza más de 10 transacciones
 * en un período de 24 horas. Esto puede indicar una tarjeta
 * comprometida siendo usada frenéticamente.
 *
 * @author mago
 */
public class VelocityRule implements FraudRule {

    private static final int MAX_TRANSACTIONS_24H = 10;

    @Override
    public Optional<FraudResult> evaluate(Transaction transaction, CustomerHistory history) {
        int count = history.transactionCount24Hours();

        if (count > MAX_TRANSACTIONS_24H) {
            return Optional.of(FraudResult.of(
                    "VELOCITY",
                    String.format("%d transactions in 24 hours exceeds limit of %d",
                            count, MAX_TRANSACTIONS_24H),
                    transaction
            ));
        }

        return Optional.empty();
    }
}