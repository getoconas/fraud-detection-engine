package com.mago.domain.service;

import com.mago.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class UnusualCountryRuleTest {

    private UnusualCountryRule rule;
    private CardNumber card;
    private Location argentina;
    private Location españa;
    private Location brasil;
    private Instant now;

    @BeforeEach
    void setUp() {
        rule = new UnusualCountryRule();
        card = CardNumber.of("1234567890123456");
        argentina = Location.of(-34.6037, -58.3816, "Argentina", "Buenos Aires");
        españa = Location.of(40.4168, -3.7038, "España", "Madrid");
        brasil = Location.of(-23.5505, -46.6333, "Brasil", "Sao Paulo");
        now = Instant.now();
    }

    @Test
    void shouldDetectFraudWhenNewCountryAndHighAmount() {
        // Cliente solo compró en Argentina
        CustomerHistory history = buildHistoryWithCountry(argentina);
        // Ahora compra en España con monto alto
        Transaction tx = buildTransaction(españa, Money.of("5000.00"));

        assertThat(rule.evaluate(tx, history)).isPresent();
    }

    @Test
    void shouldNotDetectFraudWhenNewCountryButLowAmount() {
        CustomerHistory history = buildHistoryWithCountry(argentina);
        // País nuevo, pero monto bajo → no es fraude
        Transaction tx = buildTransaction(españa, Money.of("500.00"));

        assertThat(rule.evaluate(tx, history)).isEmpty();
    }

    @Test
    void shouldNotDetectFraudWhenKnownCountryAndHighAmount() {
        CustomerHistory history = buildHistoryWithCountry(argentina);
        // Mismo país, aunque monto alto → es inusual pero no por país
        Transaction tx = buildTransaction(argentina, Money.of("5000.00"));

        assertThat(rule.evaluate(tx, history)).isEmpty();
    }

    @Test
    void shouldNotApplyWhenNoHistory() {
        CustomerHistory history = CustomerHistory.empty(card);
        Transaction tx = buildTransaction(españa, Money.of("5000.00"));

        // Sin historial, todos los países son nuevos → no aplica
        assertThat(rule.evaluate(tx, history)).isEmpty();
    }

    @Test
    void shouldIncludeCountryAndAmountInResult() {
        CustomerHistory history = buildHistoryWithCountry(argentina);
        Transaction tx = buildTransaction(brasil, Money.of("3000.00"));

        FraudResult result = rule.evaluate(tx, history).orElseThrow();

        assertThat(result.ruleName()).isEqualTo("UNUSUAL_COUNTRY");
        assertThat(result.reason()).contains("BRASIL");
        assertThat(result.reason()).contains("3000.00");
    }

    // --- Helpers ---

    private CustomerHistory buildHistoryWithCountry(Location location) {
        CustomerHistory history = CustomerHistory.empty(card);
        Transaction tx = Transaction.from(
                TransactionId.generate(), card, Money.of("200.00"),
                location, "Comercio Local", "TEST", now.minus(24, ChronoUnit.HOURS)
        );
        return history.updateWith(tx);
    }

    private Transaction buildTransaction(Location location, Money amount) {
        return Transaction.from(
                TransactionId.generate(), card, amount,
                location, "Comercio Test", "TEST", now
        );
    }
}