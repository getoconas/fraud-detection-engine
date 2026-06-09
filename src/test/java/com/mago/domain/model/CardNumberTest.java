package com.mago.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CardNumberTest {

    @Test
    void shouldCreateValidCardNumber() {
        CardNumber card = CardNumber.of("1234567890123456");
        assertThat(card.value()).isEqualTo("1234567890123456");
    }

    @Test
    void shouldSanitizeSpacesAndDashes() {
        CardNumber card = CardNumber.of("1234-5678-9012-3456");
        assertThat(card.value()).isEqualTo("1234567890123456");
    }

    @Test
    void shouldMaskCardNumber() {
        CardNumber card = CardNumber.of("1234567890123456");
        assertThat(card.masked()).isEqualTo("****3456");
    }

    @Test
    void shouldRejectNullCardNumber() {
        assertThatThrownBy(() -> CardNumber.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Card number cannot be null");
    }

    @Test
    void shouldRejectTooShortCardNumber() {
        assertThatThrownBy(() -> CardNumber.of("123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid card number format");
    }

    @Test
    void shouldRejectNonNumericCardNumber() {
        assertThatThrownBy(() -> CardNumber.of("abcd-efgh-ijkl-mnop"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid card number format");
    }
}