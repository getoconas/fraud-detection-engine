package com.mago.domain.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.Objects;

/**
 * Value Object que representa un número de tarjeta de crédito/débito.
 * <p>
 * Características:
 * <ul>
 *   <li>Inmutable: una vez creado, no puede modificarse</li>
 *   <li>Limpieza automática: elimina espacios y guiones del valor ingresado</li>
 *   <li>Validación de formato: solo dígitos, entre 13 y 19 caracteres</li>
 *   <li>Enmascaramiento: muestra solo los últimos 4 dígitos para proteger datos sensibles</li>
 *   <li>Comparable por valor: dos instancias con el mismo número son iguales</li>
 * </ul>
 * <p>
 * No implementa el algoritmo de Luhn para mantener simple el value object.
 * Esa validación puede agregarse en una capa superior si se requiere.
 *
 * @author mago
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class CardNumber {
    private final String value;

    /**
     * Constructor privado. Usar el método factory {@link #of}.
     */
    private CardNumber(String value) {
        this.value = value;
    }

    /**
     * Crea un CardNumber a partir de un String.
     * <p>
     * Elimina espacios y guiones, y valida que el resultado sea
     * una cadena de entre 13 y 19 dígitos.
     * <p>
     * Ejemplos válidos:
     * <ul>
     *   <li>"1234567890123456"</li>
     *   <li>"1234-5678-9012-3456"</li>
     *   <li>"1234 5678 9012 3456"</li>
     * </ul>
     *
     * @param value el número de tarjeta como String
     * @return una nueva instancia de CardNumber
     * @throws IllegalArgumentException si el valor es nulo, vacío o no tiene formato válido
     */
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

    /**
     * Devuelve el número de tarjeta completo sin enmascarar.
     * <p>
     * Usar con precaución: solo para procesamiento interno.
     *
     * @return el número de tarjeta como String de dígitos
     */
    public String value() {
        return value;
    }

    /**
     * Devuelve una versión enmascarada del número de tarjeta.
     * <p>
     * Muestra solo los últimos 4 dígitos, reemplazando el resto con asteriscos.
     * Si el número tiene 4 dígitos o menos, muestra todo enmascarado.
     * <p>
     * Ejemplo: "1234567890123456" → "****3456"
     *
     * @return el número enmascarado para mostrar en logs o UI
     */
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

    /**
     * Devuelve la versión enmascarada por seguridad.
     * <p>
     * Nunca muestra el número completo en logs o consola.
     */
    @Override
    public String toString() {
        return masked();
    }
}