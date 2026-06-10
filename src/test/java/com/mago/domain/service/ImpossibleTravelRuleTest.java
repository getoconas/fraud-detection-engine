package com.mago.domain.service;

import com.mago.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ImpossibleTravelRuleTest {

    private ImpossibleTravelRule rule;
    private CardNumber card;
    private Location argentina;
    private Location espania;
    private Instant now;

    @BeforeEach
    void setUp() {
        rule = new ImpossibleTravelRule();
        card = CardNumber.of("1234567890123456");
        argentina = Location.of(-34.6037, -58.3816, "Argentina", "Buenos Aires");
        espania = Location.of(40.4168, -3.7038, "España", "Madrid");
        now = Instant.now();
    }

    @Test
    void shouldDetectFraudWhenDifferentCountriesWithinOneHour() {
        // Transacción anterior en Argentina hace 30 minutos
        Transaction previousTx = buildTransaction(argentina, now.minus(30, ChronoUnit.MINUTES));
        CustomerHistory history = CustomerHistory.empty(card).updateWith(previousTx);

        // Nueva transacción en España
        Transaction currentTx = buildTransaction(espania, now);

        assertThat(rule.evaluate(currentTx, history)).isPresent();
    }

    @Test
    void shouldNotDetectFraudWhenSameCountry() {
        Transaction previousTx = buildTransaction(argentina, now.minus(10, ChronoUnit.MINUTES));
        CustomerHistory history = CustomerHistory.empty(card).updateWith(previousTx);

        // Mismo país, aunque sea en minutos
        Transaction currentTx = buildTransaction(argentina, now);

        assertThat(rule.evaluate(currentTx, history)).isEmpty();
    }

    @Test
    void shouldNotDetectFraudWhenMoreThanOneHourPassed() {
        Transaction previousTx = buildTransaction(argentina, now.minus(2, ChronoUnit.HOURS));
        CustomerHistory history = CustomerHistory.empty(card).updateWith(previousTx);

        Transaction currentTx = buildTransaction(espania, now);

        // Más de 1 hora entre países distintos → no es imposible
        assertThat(rule.evaluate(currentTx, history)).isEmpty();
    }

    @Test
    void shouldNotApplyWhenNoPreviousTransaction() {
        CustomerHistory history = CustomerHistory.empty(card);
        Transaction currentTx = buildTransaction(espania, now);

        assertThat(rule.evaluate(currentTx, history)).isEmpty();
    }

    @Test
    void shouldIncludeDistanceInResult() {
        Transaction previousTx = buildTransaction(argentina, now.minus(20, ChronoUnit.MINUTES));
        CustomerHistory history = CustomerHistory.empty(card).updateWith(previousTx);
        Transaction currentTx = buildTransaction(espania, now);

        FraudResult result = rule.evaluate(currentTx, history).orElseThrow();

        assertThat(result.ruleName()).isEqualTo("IMPOSSIBLE_TRAVEL");
        assertThat(result.reason()).contains("km apart");
        assertThat(result.reason()).contains("ARGENTINA");
        assertThat(result.reason()).contains("ESPAÑA");
    }

    // --- Helper ---

    private Transaction buildTransaction(Location location, Instant timestamp) {
        return Transaction.from(
                TransactionId.generate(), card, Money.of("500.00"),
                location, "Comercio", "TEST", timestamp
        );
    }
}