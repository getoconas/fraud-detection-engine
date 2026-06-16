package com.mago.domain.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;

/**
 * Value Object que representa el resultado de una regla de fraude.
 * <p>
 * Contiene la razón de la detección y la transacción que la disparó.
 * Es inmutable.
 *
 * @author mago
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class FraudResult {
    private final String ruleName;
    private final String reason;
    private final TransactionId transactionId;
    private final CardNumber cardNumber;
    private final Instant detectedAt;

    private FraudResult(String ruleName, String reason, TransactionId transactionId,
                        CardNumber cardNumber, Instant detectedAt) {
        this.ruleName = ruleName;
        this.reason = reason;
        this.transactionId = transactionId;
        this.cardNumber = cardNumber;
        this.detectedAt = detectedAt;
    }

    @JsonCreator
    public static FraudResult of(
            @JsonProperty("ruleName") String ruleName,
            @JsonProperty("reason") String reason,
            @JsonProperty("transaction") Transaction transaction) {
        if (ruleName == null || ruleName.isBlank()) {
            throw new IllegalArgumentException("Rule name cannot be null or blank");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason cannot be null or blank");
        }
        return new FraudResult(ruleName, reason, transaction.id(),
                transaction.cardNumber(), Instant.now());
    }

    /** @return nombre de la regla que disparó la alerta */
    public String ruleName() { return ruleName; }

    /** @return descripción de la razón del fraude */
    public String reason() { return reason; }

    /** @return ID de la transacción sospechosa */
    public TransactionId transactionId() { return transactionId; }

    /** @return tarjeta involucrada */
    public CardNumber cardNumber() { return cardNumber; }

    /** @return momento en que se detectó */
    public Instant detectedAt() { return detectedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FraudResult that = (FraudResult) o;
        return transactionId.equals(that.transactionId) && ruleName.equals(that.ruleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, ruleName);
    }

    @Override
    public String toString() {
        return "FraudResult{" +
                "rule='" + ruleName + '\'' +
                ", reason='" + reason + '\'' +
                ", card=" + cardNumber +
                '}';
    }
}