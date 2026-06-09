package com.mago.domain.model;

import java.util.Objects;

/**
 * Value Object que representa un número de tarjeta.
 * Inmutable, con validación básica de formato.
 */
public final class CardNumber {
    private final String value;

    private CardNumber(String value) {
        this.value = value;
    }

    public static CardNumber of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Card number cannot be null or blank");
        }
        String sanitized = value.replaceAll("[\\s-]", "");
        if (!sanitized.matches("\\d{13,19}")) {
            throw new IllegalArgumentException("Invalid card number format: " + value);
        }
        return new CardNumber(sanitized);
    }

    public String value() {
        return value;
    }

    public String masked() {
        if (value.length() <= 4) return "****";
        return "****" + value.substring(value.length() - 4);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardNumber that = (CardNumber) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return masked();
    }
}