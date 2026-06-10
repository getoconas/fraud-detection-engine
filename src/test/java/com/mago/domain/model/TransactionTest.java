package com.mago.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class TransactionTest {

    private final CardNumber card = CardNumber.of("1234567890123456");
    private final Money amount = Money.of("1500.00");
    private final Location location = Location.of(-34.6037, -58.3816, "Argentina", "Buenos Aires");

    @Test
    void shouldCreateTransactionWithCurrentTimestamp() {
        Transaction tx = Transaction.create(card, amount, location, "Supermercado X", "RETAIL");

        assertThat(tx.id()).isNotNull();
        assertThat(tx.cardNumber()).isEqualTo(card);
        assertThat(tx.amount()).isEqualTo(amount);
        assertThat(tx.location()).isEqualTo(location);
        assertThat(tx.merchantName()).isEqualTo("Supermercado X");
        assertThat(tx.merchantCategory()).isEqualTo("RETAIL");
        assertThat(tx.timestamp()).isCloseTo(Instant.now(), within(2, ChronoUnit.SECONDS));
    }

    @Test
    void shouldCreateTransactionWithSpecificTimestamp() {
        Instant pastMoment = Instant.parse("2026-06-01T10:00:00Z");
        TransactionId id = TransactionId.generate();

        Transaction tx = Transaction.from(
                id, card, amount, location,
                "Aeropuerto EZE", "TRAVEL", pastMoment
        );

        assertThat(tx.id()).isEqualTo(id);
        assertThat(tx.timestamp()).isEqualTo(pastMoment);
        assertThat(tx.merchantCategory()).isEqualTo("TRAVEL");
    }

    @Test
    void shouldNormalizeMerchantCategoryToUppercase() {
        Transaction tx = Transaction.create(card, amount, location, "Tienda", "food");

        assertThat(tx.merchantCategory()).isEqualTo("FOOD");
    }

    @Test
    void shouldTrimMerchantName() {
        Transaction tx = Transaction.create(card, amount, location, "  Mi Comercio  ", "RETAIL");

        assertThat(tx.merchantName()).isEqualTo("Mi Comercio");
    }

    @Test
    void shouldRejectNullMerchantName() {
        assertThatThrownBy(() -> Transaction.create(card, amount, location, null, "RETAIL"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Merchant name cannot be null");
    }

    @Test
    void shouldRejectBlankMerchantCategory() {
        assertThatThrownBy(() -> Transaction.create(card, amount, location, "Tienda", "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Merchant category cannot be null");
    }

    @Test
    void shouldBeEqualWhenSameId() {
        TransactionId sharedId = TransactionId.generate();
        Transaction tx1 = Transaction.from(sharedId, card, amount, location, "A", "RETAIL", Instant.now());
        Transaction tx2 = Transaction.from(sharedId, card, Money.of("999.99"),
                Location.of(40.4168, -3.7038, "España", "Madrid"),
                "B", "TRAVEL", Instant.now());

        // Mismo ID = misma transacción, aunque los datos difieran
        assertThat(tx1).isEqualTo(tx2);
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        Transaction tx1 = Transaction.create(card, amount, location, "A", "RETAIL");
        Transaction tx2 = Transaction.create(card, amount, location, "A", "RETAIL");

        assertThat(tx1).isNotEqualTo(tx2);
    }

    @Test
    void toStringShouldNotExposeFullCardNumber() {
        Transaction tx = Transaction.create(card, amount, location, "Tienda", "RETAIL");

        String str = tx.toString();
        assertThat(str).doesNotContain("1234567890123456");
        assertThat(str).contains("****3456");
    }
}