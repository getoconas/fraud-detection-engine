package com.mago.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithValidAmount() {
        Money money = Money.of(new BigDecimal("100.50"));
        assertThat(money.amount()).isEqualByComparingTo("100.50");
    }

    @Test
    void shouldCreateMoneyFromString() {
        Money money = Money.of("200.00");
        assertThat(money.amount()).isEqualByComparingTo("200.00");
    }

    @Test
    void shouldRejectNullAmount() {
        assertThatThrownBy(() -> Money.of((BigDecimal) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount cannot be null");
    }

    @Test
    void shouldRejectNegativeAmount() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("-10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount cannot be negative");
    }

    @Test
    void shouldMultiplyCorrectly() {
        Money original = Money.of("100.00");
        Money multiplied = original.multiply(5);
        assertThat(multiplied.amount()).isEqualByComparingTo("500.00");
    }

    @Test
    void shouldCompareGreaterThan() {
        Money hundred = Money.of("100.00");
        Money fifty = Money.of("50.00");
        assertThat(hundred.isGreaterThan(fifty)).isTrue();
        assertThat(fifty.isGreaterThan(hundred)).isFalse();
    }

    @Test
    void shouldBeEqualWhenSameAmount() {
        Money money1 = Money.of("100.00");
        Money money2 = Money.of("100.00");
        assertThat(money1).isEqualTo(money2);
    }

    @Test
    void shouldRoundToTwoDecimals() {
        Money money = Money.of(new BigDecimal("100.456"));
        assertThat(money.amount()).isEqualByComparingTo("100.46");
    }
}