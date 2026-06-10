package com.mago.domain.service;

import com.mago.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FraudDetectionEngineTest {

    private CardNumber card;
    private Location argentina;
    private Location espania;
    private Instant now;
    private List<FraudRule> allRules;

    @BeforeEach
    void setUp() {
        card = CardNumber.of("1234567890123456");
        argentina = Location.of(-34.6037, -58.3816, "Argentina", "Buenos Aires");
        espania = Location.of(40.4168, -3.7038, "España", "Madrid");
        now = Instant.now();

        allRules = List.of(
                new HighAmountRule(),
                new ImpossibleTravelRule(),
                new VelocityRule(),
                new UnusualCountryRule()
        );
    }

    @Test
    void shouldDetectNoFraudForNormalTransaction() {
        FraudDetectionEngine engine = new FraudDetectionEngine(allRules);

        // Cliente con historial normal en Argentina
        CustomerHistory history = CustomerHistory.empty(card);
        history = history.updateWith(
                buildTransaction(argentina, Money.of("200.00"), now.minus(48, ChronoUnit.HOURS)));

        // Transacción normal en Argentina con monto razonable
        Transaction tx = buildTransaction(argentina, Money.of("300.00"), now);

        List<FraudResult> results = engine.detect(tx, history);

        assertThat(results).isEmpty();
        assertThat(engine.isFraudulent(tx, history)).isFalse();
    }

    @Test
    void shouldDetectSingleFraudRule() {
        FraudDetectionEngine engine = new FraudDetectionEngine(allRules);

        CustomerHistory history = CustomerHistory.empty(card);
        history = history.updateWith(
                buildTransaction(argentina, Money.of("100.00"), now.minus(1, ChronoUnit.HOURS)));

        // Transacción en España 30 minutos después (viaje imposible)
        Transaction tx = buildTransaction(espania, Money.of("200.00"), now.minus(30, ChronoUnit.MINUTES));

        List<FraudResult> results = engine.detect(tx, history);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).ruleName()).isEqualTo("IMPOSSIBLE_TRAVEL");
    }

    @Test
    void shouldDetectMultipleFraudRulesForSameTransaction() {
        FraudDetectionEngine engine = new FraudDetectionEngine(allRules);

        CustomerHistory history = CustomerHistory.empty(card);
        history = history.updateWith(
                buildTransaction(argentina, Money.of("100.00"), now.minus(30, ChronoUnit.MINUTES)));

        // Transacción en España con monto muy alto → viaje imposible + país inusual + monto alto
        Transaction tx = buildTransaction(espania, Money.of("8000.00"), now);

        List<FraudResult> results = engine.detect(tx, history);

        assertThat(results).hasSize(3);

        List<String> ruleNames = results.stream().map(FraudResult::ruleName).toList();
        assertThat(ruleNames).containsExactlyInAnyOrder(
                "HIGH_AMOUNT", "IMPOSSIBLE_TRAVEL", "UNUSUAL_COUNTRY");
    }

    @Test
    void shouldWorkWithSingleRule() {
        FraudDetectionEngine engine = new FraudDetectionEngine(
                List.of(new HighAmountRule()));

        CustomerHistory history = CustomerHistory.empty(card);
        history = history.updateWith(
                buildTransaction(argentina, Money.of("100.00"), now.minus(1, ChronoUnit.HOURS)));

        Transaction tx = buildTransaction(argentina, Money.of("600.00"), now);

        List<FraudResult> results = engine.detect(tx, history);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).ruleName()).isEqualTo("HIGH_AMOUNT");
    }

    @Test
    void shouldRejectNullRules() {
        assertThatThrownBy(() -> new FraudDetectionEngine(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rules list cannot be null");
    }

    @Test
    void shouldRejectEmptyRules() {
        assertThatThrownBy(() -> new FraudDetectionEngine(Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rules list cannot be null or empty");
    }

    @Test
    void shouldRejectNullTransaction() {
        FraudDetectionEngine engine = new FraudDetectionEngine(allRules);
        CustomerHistory history = CustomerHistory.empty(card);

        assertThatThrownBy(() -> engine.detect(null, history))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transaction cannot be null");
    }

    @Test
    void shouldRejectNullHistory() {
        FraudDetectionEngine engine = new FraudDetectionEngine(allRules);
        Transaction tx = buildTransaction(argentina, Money.of("100.00"), now);

        assertThatThrownBy(() -> engine.detect(tx, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("History cannot be null");
    }

    @Test
    void shouldReturnUnmodifiableList() {
        FraudDetectionEngine engine = new FraudDetectionEngine(allRules);
        CustomerHistory history = CustomerHistory.empty(card);
        Transaction tx = buildTransaction(espania, Money.of("5000.00"), now);

        List<FraudResult> results = engine.detect(tx, history);

        // La lista devuelta no se puede modificar
        assertThatThrownBy(() -> results.add(FraudResult.of("TEST", "test", tx)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void isFraudulentShouldReturnTrueWhenFraudDetected() {
        FraudDetectionEngine engine = new FraudDetectionEngine(allRules);

        CustomerHistory history = CustomerHistory.empty(card);
        history = history.updateWith(
                buildTransaction(argentina, Money.of("50.00"), now.minus(30, ChronoUnit.MINUTES)));

        Transaction tx = buildTransaction(espania, Money.of("100.00"), now);

        assertThat(engine.isFraudulent(tx, history)).isTrue();
    }

    @Test
    void shouldExposeConfiguredRules() {
        List<FraudRule> rules = List.of(new HighAmountRule(), new VelocityRule());
        FraudDetectionEngine engine = new FraudDetectionEngine(rules);

        assertThat(engine.rules()).hasSize(2);
        assertThat(engine.rules().get(0)).isInstanceOf(HighAmountRule.class);
        assertThat(engine.rules().get(1)).isInstanceOf(VelocityRule.class);
    }

    // --- Helpers ---

    private Transaction buildTransaction(Location location, Money amount, Instant timestamp) {
        return Transaction.from(
                TransactionId.generate(), card, amount,
                location, "Comercio Test", "TEST", timestamp
        );
    }
}