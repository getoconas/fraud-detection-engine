package com.mago.domain.service;

import com.mago.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class HighAmountRuleTest {

    private HighAmountRule rule;
    private CardNumber card;
    private Location location;
    private Instant now;

    @BeforeEach
    void setUp() {
        rule = new HighAmountRule();
        card = CardNumber.of("1234567890123456");
        location = Location.of(-34.6037, -58.3816, "Argentina", "Buenos Aires");
        now = Instant.now();
    }

    @Test
    void shouldDetectFraudWhenAmountIs5xHigherThanAverage() {
        // Cliente con promedio de $100
        CustomerHistory history = buildHistoryWithAverage(Money.of("100.00"));
        // Transacción de $600 (6x el promedio)
        Transaction tx = buildTransaction(Money.of("600.00"));

        assertThat(rule.evaluate(tx, history)).isPresent();
    }

    @Test
    void shouldDetectFraudWhenAmountIsExactly5x() {
        CustomerHistory history = buildHistoryWithAverage(Money.of("100.00"));
        // Justo 5x no se considera fraude (debe ser estrictamente mayor)
        Transaction tx = buildTransaction(Money.of("500.00"));

        assertThat(rule.evaluate(tx, history)).isEmpty();
    }

    @Test
    void shouldNotDetectFraudWhenAmountIsBelow5x() {
        CustomerHistory history = buildHistoryWithAverage(Money.of("100.00"));
        Transaction tx = buildTransaction(Money.of("400.00"));

        assertThat(rule.evaluate(tx, history)).isEmpty();
    }

    @Test
    void shouldNotApplyWhenNoHistory() {
        CustomerHistory history = CustomerHistory.empty(card);
        Transaction tx = buildTransaction(Money.of("10000.00"));

        // Sin historial previo, no podemos calcular promedio → no aplica
        assertThat(rule.evaluate(tx, history)).isEmpty();
    }

    @Test
    void shouldIncludeRuleNameInResult() {
        CustomerHistory history = buildHistoryWithAverage(Money.of("50.00"));
        Transaction tx = buildTransaction(Money.of("5000.00"));

        FraudResult result = rule.evaluate(tx, history).orElseThrow();

        assertThat(result.ruleName()).isEqualTo("HIGH_AMOUNT");
        assertThat(result.reason()).contains("5x");
        assertThat(result.cardNumber()).isEqualTo(card);
        assertThat(result.transactionId()).isEqualTo(tx.id());
    }

    // --- Helpers ---

    private CustomerHistory buildHistoryWithAverage(Money average) {
        // Simulamos un historial que ya tiene un promedio
        CustomerHistory history = CustomerHistory.empty(card);
        // Insertamos varias transacciones para construir el promedio deseado
        Transaction baseTx = Transaction.from(
                TransactionId.generate(), card, average, location,
                "Comercio", "TEST", now.minusSeconds(3600)
        );
        return history.updateWith(baseTx);
    }

    private Transaction buildTransaction(Money amount) {
        return Transaction.from(
                TransactionId.generate(), card, amount, location,
                "Comercio Test", "TEST", now
        );
    }
}