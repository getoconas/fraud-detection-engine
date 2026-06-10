package com.mago.domain.service;

import com.mago.domain.model.CustomerHistory;
import com.mago.domain.model.FraudResult;
import com.mago.domain.model.Transaction;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Regla de fraude por viaje imposible.
 * <p>
 * Detecta cuando dos transacciones consecutivas ocurren en países
 * diferentes con menos de 1 hora de diferencia entre ellas.
 * Es físicamente imposible viajar entre ciertos países en ese tiempo.
 * <p>
 * No aplica si el cliente no tiene transacciones previas.
 *
 * @author mago
 */
public class ImpossibleTravelRule implements FraudRule {

    private static final long MAX_HOURS_BETWEEN_COUNTRIES = 1;

    @Override
    public Optional<FraudResult> evaluate(Transaction transaction, CustomerHistory history) {
        Transaction lastTx = history.lastTransaction();

        // Si es la primera transacción, no podemos comparar
        if (lastTx == null) {
            return Optional.empty();
        }

        // Si es el mismo país, no hay problema
        if (transaction.location().isSameCountry(lastTx.location())) {
            return Optional.empty();
        }

        // Calcular horas entre transacciones
        long hoursBetween = ChronoUnit.HOURS.between(
                lastTx.timestamp(), transaction.timestamp());

        if (hoursBetween < MAX_HOURS_BETWEEN_COUNTRIES) {
            double distanceKm = lastTx.location().distanceKm(transaction.location());
            return Optional.of(FraudResult.of(
                    "IMPOSSIBLE_TRAVEL",
                    String.format(
                            "Transaction in %s just %d minutes after one in %s (%.0f km apart)",
                            transaction.location().country(),
                            ChronoUnit.MINUTES.between(lastTx.timestamp(), transaction.timestamp()),
                            lastTx.location().country(),
                            distanceKm),
                    transaction
            ));
        }

        return Optional.empty();
    }
}