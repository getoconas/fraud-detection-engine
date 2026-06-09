package com.mago.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object que representa un monto monetario.
 * <p>
 * Características:
 * <ul>
 *   <li>Inmutable: una vez creado, no puede modificarse</li>
 *   <li>Nunca negativo: no permite montos menores a cero</li>
 *   <li>Precisión de 2 decimales: redondea automáticamente usando {@link RoundingMode#HALF_UP}</li>
 *   <li>Comparable por valor: dos instancias con el mismo monto son iguales</li>
 * </ul>
 * <p>
 * Ejemplo de uso:
 * <pre>{@code
 *   Money precio = Money.of("1500.00");
 *   Money total = precio.multiply(3);
 *   boolean esCaro = total.isGreaterThan(Money.of("4000.00"));
 * }</pre>
 *
 * @author mago
 */
public final class Money {
    private final BigDecimal amount;

    /**
     * Constructor privado. Usar los métodos factory {@link #of} para crear instancias.
     */
    private Money(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Crea un Money a partir de un {@link BigDecimal}.
     * <p>
     * Valida que no sea nulo ni negativo. Redondea a 2 decimales.
     *
     * @param amount el monto como BigDecimal
     * @return una nueva instancia de Money
     * @throws IllegalArgumentException si el monto es nulo o negativo
     */
    public static Money of(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative: " + amount);
        }
        return new Money(amount.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Crea un Money a partir de un String.
     * <p>
     * Útil para creación rápida en tests o cuando se recibe JSON.
     *
     * @param amount el monto como String (ej: "1500.00")
     * @return una nueva instancia de Money
     * @throws NumberFormatException si el string no es un número válido
     * @throws IllegalArgumentException si el monto es negativo
     */
    public static Money of(String amount) {
        return of(new BigDecimal(amount));
    }

    /**
     * Devuelve un Money que representa cero (0.00).
     *
     * @return Money con valor 0.00
     */
    public static Money zero() {
        return new Money(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Suma este Money con otro y devuelve un nuevo Money con el resultado.
     * <p>
     * No modifica la instancia actual (inmutabilidad).
     *
     * @param other el Money a sumar
     * @return un nuevo Money con la suma de ambos montos
     */
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    /**
     * Multiplica este Money por un factor entero y devuelve un nuevo Money.
     * <p>
     * Útil para reglas de fraude: "monto 5x mayor al promedio".
     *
     * @param factor el multiplicador entero
     * @return un nuevo Money con el resultado de la multiplicación
     */
    public Money multiply(int factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)));
    }

    /**
     * Compara si este Money es estrictamente mayor que otro.
     *
     * @param other el Money a comparar
     * @return true si este monto es mayor que el otro
     */
    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Devuelve el valor del monto como {@link BigDecimal}.
     *
     * @return el monto
     */
    public BigDecimal amount() {
        return amount;
    }

    /**
     * Compara por valor. Dos Money son iguales si tienen el mismo monto.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}