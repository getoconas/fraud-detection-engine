package com.mago.domain.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Agregado que representa el historial de transacciones de un cliente.
 * <p>
 * Mantiene información calculada para detectar fraudes:
 * <ul>
 *   <li>Promedio de monto en los últimos 30 días</li>
 *   <li>Países visitados frecuentemente</li>
 *   <li>Última transacción registrada</li>
 *   <li>Conteo de transacciones en ventanas de tiempo</li>
 * </ul>
 * <p>
 * Es inmutable hacia afuera: cada vez que se agrega una transacción,
 * se devuelve un <strong>nuevo</strong> CustomerHistory con los datos actualizados.
 * Esto evita efectos secundarios y facilita el Event Sourcing.
 *
 * @author mago
 */
public final class CustomerHistory {
    private final CardNumber cardNumber;
    private final Money averageAmount30Days;
    private final List<String> frequentCountries;
    private final Transaction lastTransaction;
    private final int transactionCount24Hours;

    /**
     * Constructor privado. Usar {@link #empty} o {@link #updateWith}.
     */
    private CustomerHistory(CardNumber cardNumber, Money averageAmount30Days,
                            List<String> frequentCountries, Transaction lastTransaction,
                            int transactionCount24Hours) {
        this.cardNumber = cardNumber;
        this.averageAmount30Days = averageAmount30Days;
        this.frequentCountries = List.copyOf(frequentCountries); // inmutable
        this.lastTransaction = lastTransaction;
        this.transactionCount24Hours = transactionCount24Hours;
    }

    /**
     * Crea un historial vacío para un cliente nuevo.
     *
     * @param cardNumber número de tarjeta del cliente
     * @return un nuevo CustomerHistory con valores iniciales en cero
     */
    public static CustomerHistory empty(CardNumber cardNumber) {
        return new CustomerHistory(
                cardNumber,
                Money.zero(),
                List.of(),
                null,
                0
        );
    }

    /**
     * Devuelve un <strong>nuevo</strong> CustomerHistory con la transacción incorporada.
     * <p>
     * No modifica la instancia actual. Actualiza:
     * <ul>
     *   <li>Promedio de 30 días (aproximación simple)</li>
     *   <li>Lista de países frecuentes (últimos 5 únicos)</li>
     *   <li>Última transacción</li>
     *   <li>Conteo de transacciones en 24 horas</li>
     * </ul>
     *
     * @param transaction la nueva transacción a incorporar
     * @return un nuevo CustomerHistory actualizado
     */
    public CustomerHistory updateWith(Transaction transaction) {
        if (!this.cardNumber.equals(transaction.cardNumber())) {
            throw new IllegalArgumentException(
                    "Transaction card " + transaction.cardNumber() +
                            " does not match history card " + this.cardNumber);
        }

        // Nuevo promedio aproximado
        Money newAverage = calculateNewAverage(transaction.amount());

        // Actualizar países frecuentes (mantiene últimos 5 únicos)
        List<String> newCountries = new ArrayList<>(this.frequentCountries);
        String country = transaction.location().country();
        newCountries.remove(country); // lo movemos al final si ya existía
        newCountries.add(country);
        if (newCountries.size() > 5) {
            newCountries.remove(0);
        }

        // Contar transacciones en últimas 24 horas
        int newCount24h = calculateTransactionsLast24Hours(transaction.timestamp());

        return new CustomerHistory(
                this.cardNumber,
                newAverage,
                newCountries,
                transaction,
                newCount24h
        );
    }

    /**
     * Calcula el nuevo promedio de forma aproximada.
     * <p>
     * Fórmula simple: (promedioAnterior * 29 + nuevoMonto) / 30.
     * Es una aproximación, no guarda todas las transacciones de 30 días.
     */
    private Money calculateNewAverage(Money newAmount) {
        if (averageAmount30Days.equals(Money.zero())) {
            return newAmount;
        }
        // (promedio * 29 + nuevo) / 30
        Money total = averageAmount30Days.multiply(29).add(newAmount);
        return Money.of(
                total.amount()
                        .divide(java.math.BigDecimal.valueOf(30), 2, java.math.RoundingMode.HALF_UP)
        );
    }

    /**
     * Cuenta cuántas transacciones ocurrieron en las últimas 24 horas.
     * <p>
     * Versión simplificada: como no guardamos todas las transacciones,
     * incrementa si la última fue hace menos de 24 horas, reinicia si no.
     */
    private int calculateTransactionsLast24Hours(Instant currentTimestamp) {
        if (lastTransaction == null) {
            return 1;
        }
        long hoursSinceLast = ChronoUnit.HOURS.between(lastTransaction.timestamp(), currentTimestamp);
        if (hoursSinceLast < 24) {
            return transactionCount24Hours + 1;
        }
        return 1; // reinicia el conteo si pasaron 24 horas o más
    }

    // --- Getters ---

    /** @return número de tarjeta del cliente */
    public CardNumber cardNumber() { return cardNumber; }

    /** @return monto promedio de los últimos 30 días */
    public Money averageAmount30Days() { return averageAmount30Days; }

    /** @return lista inmutable de los últimos 5 países visitados */
    public List<String> frequentCountries() { return frequentCountries; }

    /** @return última transacción registrada, puede ser null si es nuevo */
    public Transaction lastTransaction() { return lastTransaction; }

    /** @return cantidad de transacciones en las últimas 24 horas */
    public int transactionCount24Hours() { return transactionCount24Hours; }

    /**
     * Verifica si un país está en la lista de países frecuentes.
     *
     * @param country país a verificar
     * @return true si el país ya fue visitado antes
     */
    public boolean hasVisitedCountry(String country) {
        return frequentCountries.contains(country.toUpperCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerHistory that = (CustomerHistory) o;
        return cardNumber.equals(that.cardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber);
    }

    @Override
    public String toString() {
        return "CustomerHistory{" +
                "card=" + cardNumber +
                ", avg30d=" + averageAmount30Days +
                ", countries=" + frequentCountries +
                ", tx24h=" + transactionCount24Hours +
                '}';
    }
}