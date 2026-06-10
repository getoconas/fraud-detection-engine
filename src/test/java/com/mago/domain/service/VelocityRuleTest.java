package com.mago.domain.service;

import com.mago.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class VelocityRuleTest {

    private VelocityRule rule;
    private CardNumber card;
    private Location location;
    private Instant now;

    @BeforeEach
    void setUp() {
        rule = new VelocityRule();
        card = CardNumber.of("1234567890123456");
        location = Location.of(-34.6037, -58.3816, "Argentina", "Buenos Aires");
        now = Instant.now();
    }

    @Test
    void shouldDetectFraudWhenMoreThan10TransactionsIn24Hours() {
        CustomerHistory history = buildHistoryWithTransactionCount(11);

        Transaction tx = buildTransaction(now);

        assertThat(rule.evaluate(tx, history)).isPresent();
    }

    @Test
    void shouldNotDetectFraudWithExactly10Transactions() {
        CustomerHistory history = buildHistoryWithTransactionCount(10);

        Transaction tx = buildTransaction(now);

        // 10 transacciones → justo en el límite, no es fraude
        assertThat(rule.evaluate(tx, history)).isEmpty();
    }

    @Test
    void shouldNotDetectFraudWithFewTransactions() {
        CustomerHistory history = buildHistoryWithTransactionCount(3);

        Transaction tx = buildTransaction(now);

        assertThat(rule.evaluate(tx, history)).isEmpty();
    }

    @Test
    void shouldNotApplyWhenNoHistory() {
        CustomerHistory history = CustomerHistory.empty(card);
        Transaction tx = buildTransaction(now);

        assertThat(rule.evaluate(tx, history)).isEmpty();
    }

    @Test
    void shouldIncludeCountInResult() {
        CustomerHistory history = buildHistoryWithTransactionCount(15);
        Transaction tx = buildTransaction(now);

        FraudResult result = rule.evaluate(tx, history).orElseThrow();

        assertThat(result.ruleName()).isEqualTo("VELOCITY");
        assertThat(result.reason()).contains("15");
        assertThat(result.reason()).contains("10");
    }

    // --- Helpers ---

    private CustomerHistory buildHistoryWithTransactionCount(int count) {
        CustomerHistory history = CustomerHistory.empty(card);
        for (int i = 0; i < count; i++) {
            Transaction tx = Transaction.from(
                    TransactionId.generate(), card, Money.of("50.00"),
                    location, "Comercio " + i, "TEST",
                    now.minus(1, ChronoUnit.HOURS).plus(i, ChronoUnit.MINUTES)
            );
            history = history.updateWith(tx);
        }
        return history;
    }

    private Transaction buildTransaction(Instant timestamp) {
        return Transaction.from(
                TransactionId.generate(), card, Money.of("100.00"),
                location, "Comercio Test", "TEST", timestamp
        );
    }
}