package com.mago.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad que representa una transacción financiera.
 * <p>
 * Características:
 * <ul>
 *   <li>Tiene identidad propia a través de un {@link TransactionId}</li>
 *   <li>Contiene value objects: {@link Money}, {@link CardNumber}, {@link Location}</li>
 *   <li>Inmutable: una vez creada, no cambia (una transacción real no se modifica)</li>
 *   <li>Registra el momento exacto con {@link Instant} en UTC</li>
 * </ul>
 * <p>
 * Es el evento de entrada al sistema. Llega desde la API y se publica a Kafka
 * para que el motor de fraude la evalúe.
 *
 * @author mago
 */
public final class Transaction {
    private final TransactionId id;
    private final CardNumber cardNumber;
    private final Money amount;
    private final Location location;
    private final String merchantName;
    private final String merchantCategory;
    private final Instant timestamp;

    /**
     * Constructor privado. Usar el método factory {@link #create}.
     */
    private Transaction(TransactionId id, CardNumber cardNumber, Money amount,
                        Location location, String merchantName,
                        String merchantCategory, Instant timestamp) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.location = location;
        this.merchantName = merchantName;
        this.merchantCategory = merchantCategory;
        this.timestamp = timestamp;
    }

    /**
     * Crea una nueva transacción con los datos requeridos.
     * <p>
     * Genera automáticamente un ID único y asigna el timestamp actual en UTC.
     *
     * @param cardNumber       número de tarjeta usada
     * @param amount           monto de la transacción
     * @param location         ubicación donde se realizó
     * @param merchantName     nombre del comercio
     * @param merchantCategory categoría del comercio (ej: "RETAIL", "TRAVEL", "FOOD")
     * @return una nueva instancia de Transaction
     * @throws IllegalArgumentException si merchantName o merchantCategory son nulos/vacíos
     */
    public static Transaction create(CardNumber cardNumber, Money amount,
                                     Location location, String merchantName,
                                     String merchantCategory) {
        if (merchantName == null || merchantName.isBlank()) {
            throw new IllegalArgumentException("Merchant name cannot be null or blank");
        }
        if (merchantCategory == null || merchantCategory.isBlank()) {
            throw new IllegalArgumentException("Merchant category cannot be null or blank");
        }
        return new Transaction(
                TransactionId.generate(),
                cardNumber,
                amount,
                location,
                merchantName.trim(),
                merchantCategory.trim().toUpperCase(),
                Instant.now()
        );
    }

    /**
     * Crea una transacción con timestamp específico.
     * <p>
     * Útil para testing o para reproducir escenarios históricos.
     *
     * @param id               ID de la transacción
     * @param cardNumber       número de tarjeta
     * @param amount           monto
     * @param location         ubicación
     * @param merchantName     nombre del comercio
     * @param merchantCategory categoría del comercio
     * @param timestamp        momento de la transacción
     * @return una nueva instancia de Transaction
     */
    public static Transaction from(TransactionId id, CardNumber cardNumber, Money amount,
                                   Location location, String merchantName,
                                   String merchantCategory, Instant timestamp) {
        if (merchantName == null || merchantName.isBlank()) {
            throw new IllegalArgumentException("Merchant name cannot be null or blank");
        }
        if (merchantCategory == null || merchantCategory.isBlank()) {
            throw new IllegalArgumentException("Merchant category cannot be null or blank");
        }
        return new Transaction(
                id,
                cardNumber,
                amount,
                location,
                merchantName.trim(),
                merchantCategory.trim().toUpperCase(),
                timestamp
        );
    }

    // --- Getters ---

    /** @return ID único de la transacción */
    public TransactionId id() { return id; }

    /** @return número de tarjeta usada */
    public CardNumber cardNumber() { return cardNumber; }

    /** @return monto de la transacción */
    public Money amount() { return amount; }

    /** @return ubicación donde se realizó */
    public Location location() { return location; }

    /** @return nombre del comercio */
    public String merchantName() { return merchantName; }

    /** @return categoría del comercio */
    public String merchantCategory() { return merchantCategory; }

    /** @return momento de la transacción en UTC */
    public Instant timestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", card=" + cardNumber +
                ", amount=" + amount +
                ", location=" + location +
                ", merchant='" + merchantName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}