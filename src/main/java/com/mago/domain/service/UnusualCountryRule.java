package com.mago.domain.service;

import com.mago.domain.model.CustomerHistory;
import com.mago.domain.model.FraudResult;
import com.mago.domain.model.Money;
import com.mago.domain.model.Transaction;

import java.util.Optional;

/**
 * Regla de fraude por país inusual con monto elevado.
 * <p>
 * Detecta cuando una transacción ocurre en un país que el cliente
 * nunca visitó antes Y además el monto es alto (más de $1000).
 * <p>
 * La combinación de país desconocido + monto alto es un fuerte
 * indicador de fraude.
 *
 * @author mago
 */
public class UnusualCountryRule implements FraudRule {

    private static final Money HIGH_AMOUNT_THRESHOLD = Money.of("1000.00");

    @Override
    public Optional<FraudResult> evaluate(Transaction transaction, CustomerHistory history) {
        // Si el cliente no tiene historial previo, no podemos determinar
        // si un país es "inusual" para él
        if (history.lastTransaction() == null) {
            return Optional.empty();
        }

        String country = transaction.location().country();

        // Si ya visitó este país, no es sospechoso
        if (history.hasVisitedCountry(country)) {
            return Optional.empty();
        }

        // Si nunca visitó el país Y el monto es alto → fraude
        if (transaction.amount().isGreaterThan(HIGH_AMOUNT_THRESHOLD)) {
            return Optional.of(FraudResult.of(
                    "UNUSUAL_COUNTRY",
                    String.format("First transaction in %s with high amount of %s",
                            country, transaction.amount()),
                    transaction
            ));
        }

        return Optional.empty();
    }
}