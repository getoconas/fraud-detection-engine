package com.mago.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador único de una transacción.
 * <p>
 * Encapsula un {@link UUID} generado automáticamente.
 * Es inmutable y comparable por valor.
 *
 * @author mago
 */
public final class TransactionId {
    private final UUID value;

    private TransactionId(UUID value) {
        this.value = value;
    }

    /**
     * Genera un nuevo ID único.
     *
     * @return una nueva instancia de TransactionId
     */
    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    /**
     * Crea un TransactionId a partir de un String.
     *
     * @param value el UUID como String
     * @return una nueva instancia de TransactionId
     * @throws IllegalArgumentException si el formato no es válido
     */
    public static TransactionId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or blank");
        }
        return new TransactionId(UUID.fromString(value));
    }

    /** @return el UUID interno */
    public UUID value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionId that = (TransactionId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}