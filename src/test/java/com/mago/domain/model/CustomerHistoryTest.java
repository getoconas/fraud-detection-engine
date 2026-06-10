package com.mago.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerHistoryTest {

    private CardNumber card;
    private Location argentina;
    private Location espania;
    private Location brasil;

    @BeforeEach
    void setUp() {
        card = CardNumber.of("1234567890123456");
        argentina = Location.of(-34.6037, -58.3816, "Argentina", "Buenos Aires");
        espania = Location.of(40.4168, -3.7038, "España", "Madrid");
        brasil = Location.of(-23.5505, -46.6333, "Brasil", "Sao Paulo");
    }

    @Test
    void shouldCreateEmptyHistory() {
        CustomerHistory history = CustomerHistory.empty(card);

        assertThat(history.cardNumber()).isEqualTo(card);
        assertThat(history.averageAmount30Days()).isEqualTo(Money.zero());
        assertThat(history.frequentCountries()).isEmpty();
        assertThat(history.lastTransaction()).isNull();
        assertThat(history.transactionCount24Hours()).isZero();
    }

    @Test
    void shouldUpdateHistoryWithNewTransaction() {
        CustomerHistory empty = CustomerHistory.empty(card);
        Transaction tx = createTransaction(Instant.now(), argentina, "1500.00");

        CustomerHistory updated = empty.updateWith(tx);

        assertThat(updated.lastTransaction()).isEqualTo(tx);
        assertThat(updated.averageAmount30Days()).isEqualTo(Money.of("1500.00"));
        assertThat(updated.frequentCountries()).containsExactly("ARGENTINA");
        assertThat(updated.transactionCount24Hours()).isEqualTo(1);
    }

    @Test
    void shouldNotModifyOriginalHistory() {
        CustomerHistory original = CustomerHistory.empty(card);
        Transaction tx = createTransaction(Instant.now(), argentina, "100.00");

        original.updateWith(tx);

        // La instancia original sigue vacía
        assertThat(original.lastTransaction()).isNull();
        assertThat(original.averageAmount30Days()).isEqualTo(Money.zero());
    }

    @Test
    void shouldUpdateAverageWithMultipleTransactions() {
        CustomerHistory history = CustomerHistory.empty(card);

        // Primera: $1000
        history = history.updateWith(createTransaction(Instant.now(), argentina, "1000.00"));
        assertThat(history.averageAmount30Days().amount()).isEqualByComparingTo("1000.00");

        // Segunda: $2000 → promedio aproximado: (1000*29 + 2000)/30 = 1033.33
        history = history.updateWith(createTransaction(Instant.now().plus(1, ChronoUnit.HOURS),
                argentina, "2000.00"));
        assertThat(history.averageAmount30Days().amount()).isEqualByComparingTo("1033.33");
    }

    @Test
    void shouldTrackFrequentCountries() {
        CustomerHistory history = CustomerHistory.empty(card);

        history = history.updateWith(createTransaction(Instant.now(), argentina, "100.00"));
        history = history.updateWith(createTransaction(Instant.now(), espania, "200.00"));
        history = history.updateWith(createTransaction(Instant.now(), brasil, "300.00"));

        assertThat(history.frequentCountries())
                .containsExactly("ARGENTINA", "ESPAÑA", "BRASIL");
    }

    @Test
    void shouldKeepOnlyLastFiveUniqueCountries() {
        CustomerHistory history = CustomerHistory.empty(card);
        Instant now = Instant.now();

        history = history.updateWith(createTransaction(now, argentina, "100.00"));
        history = history.updateWith(createTransaction(now, espania, "100.00"));
        history = history.updateWith(createTransaction(now, brasil, "100.00"));
        history = history.updateWith(createTransaction(now,
                Location.of(19.4326, -99.1332, "México", "CDMX"), "100.00"));
        history = history.updateWith(createTransaction(now,
                Location.of(48.8566, 2.3522, "Francia", "París"), "100.00"));
        history = history.updateWith(createTransaction(now,
                Location.of(51.5074, -0.1278, "Reino Unido", "Londres"), "100.00"));

        // Solo 5 países, ARGENTINA debería haber salido
        assertThat(history.frequentCountries()).hasSize(5);
        assertThat(history.frequentCountries()).doesNotContain("ARGENTINA");
        assertThat(history.frequentCountries()).contains("REINO UNIDO");
    }

    @Test
    void shouldMoveExistingCountryToEndWhenVisitedAgain() {
        CustomerHistory history = CustomerHistory.empty(card);
        Instant now = Instant.now();

        history = history.updateWith(createTransaction(now, argentina, "100.00"));
        history = history.updateWith(createTransaction(now, espania, "100.00"));
        // Argentina otra vez: debe moverse al final
        history = history.updateWith(createTransaction(now, argentina, "200.00"));

        assertThat(history.frequentCountries())
                .containsExactly("ESPAÑA", "ARGENTINA");
    }

    @Test
    void shouldCountTransactionsIn24Hours() {
        CustomerHistory history = CustomerHistory.empty(card);
        Instant now = Instant.now();

        history = history.updateWith(createTransaction(now, argentina, "100.00"));
        assertThat(history.transactionCount24Hours()).isEqualTo(1);

        history = history.updateWith(createTransaction(now.plus(1, ChronoUnit.HOURS),
                argentina, "200.00"));
        assertThat(history.transactionCount24Hours()).isEqualTo(2);

        history = history.updateWith(createTransaction(now.plus(2, ChronoUnit.HOURS),
                argentina, "300.00"));
        assertThat(history.transactionCount24Hours()).isEqualTo(3);
    }

    @Test
    void shouldResetCountAfter24Hours() {
        CustomerHistory history = CustomerHistory.empty(card);
        Instant now = Instant.now();

        history = history.updateWith(createTransaction(now, argentina, "100.00"));
        history = history.updateWith(createTransaction(now.plus(1, ChronoUnit.HOURS),
                argentina, "200.00"));

        // 25 horas después: se reinicia el conteo
        history = history.updateWith(createTransaction(now.plus(25, ChronoUnit.HOURS),
                argentina, "300.00"));
        assertThat(history.transactionCount24Hours()).isEqualTo(1);
    }

    @Test
    void shouldDetectVisitedCountry() {
        CustomerHistory history = CustomerHistory.empty(card);
        history = history.updateWith(createTransaction(Instant.now(), argentina, "100.00"));

        assertThat(history.hasVisitedCountry("ARGENTINA")).isTrue();
        assertThat(history.hasVisitedCountry("argentina")).isTrue(); // case insensitive
        assertThat(history.hasVisitedCountry("BRASIL")).isFalse();
    }

    @Test
    void shouldRejectTransactionFromDifferentCard() {
        CustomerHistory history = CustomerHistory.empty(card);
        CardNumber otherCard = CardNumber.of("9999888877776666");
        Transaction tx = Transaction.create(otherCard, Money.of("100.00"), argentina, "X", "RETAIL");

        assertThatThrownBy(() -> history.updateWith(tx))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not match history card");
    }

    // --- Helper ---

    private Transaction createTransaction(Instant timestamp, Location location, String amount) {
        return Transaction.from(
                TransactionId.generate(),
                card,
                Money.of(amount),
                location,
                "Comercio Test",
                "TEST",
                timestamp
        );
    }
}