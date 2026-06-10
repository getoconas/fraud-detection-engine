package com.mago.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionIdTest {

    @Test
    void shouldGenerateUniqueId() {
        TransactionId id1 = TransactionId.generate();
        TransactionId id2 = TransactionId.generate();

        assertThat(id1).isNotNull();
        assertThat(id2).isNotNull();
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    void shouldCreateFromValidString() {
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        TransactionId id = TransactionId.of(uuid);

        assertThat(id.value().toString()).isEqualTo(uuid);
    }

    @Test
    void shouldRejectNullString() {
        assertThatThrownBy(() -> TransactionId.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transaction ID cannot be null");
    }

    @Test
    void shouldRejectInvalidUuidFormat() {
        assertThatThrownBy(() -> TransactionId.of("esto-no-es-uuid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldBeEqualWhenSameUuid() {
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        TransactionId id1 = TransactionId.of(uuid);
        TransactionId id2 = TransactionId.of(uuid);

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}