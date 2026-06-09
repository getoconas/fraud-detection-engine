package com.mago.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

class LocationTest {

    @Test
    void shouldCreateValidLocation() {
        Location location = Location.of(-34.6037, -58.3816, "Argentina", "Buenos Aires");
        assertThat(location.country()).isEqualTo("ARGENTINA");
        assertThat(location.city()).isEqualTo("Buenos Aires");
    }

    @Test
    void shouldRejectInvalidLatitude() {
        assertThatThrownBy(() -> Location.of(100, -58.3816, "Argentina", "Buenos Aires"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid latitude");
    }

    @Test
    void shouldRejectEmptyCountry() {
        assertThatThrownBy(() -> Location.of(-34.6037, -58.3816, "", "Buenos Aires"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Country cannot be null");
    }

    @Test
    void shouldDetectSameCountry() {
        Location bsAs = Location.of(-34.6037, -58.3816, "Argentina", "Buenos Aires");
        Location cordoba = Location.of(-31.4201, -64.1888, "Argentina", "Córdoba");
        assertThat(bsAs.isSameCountry(cordoba)).isTrue();
    }

    @Test
    void shouldDetectDifferentCountry() {
        Location bsAs = Location.of(-34.6037, -58.3816, "Argentina", "Buenos Aires");
        Location madrid = Location.of(40.4168, -3.7038, "España", "Madrid");
        assertThat(bsAs.isSameCountry(madrid)).isFalse();
    }

    @Test
    void shouldCalculateDistanceBetweenCities() {
        Location bsAs = Location.of(-34.6037, -58.3816, "Argentina", "Buenos Aires");
        Location madrid = Location.of(40.4168, -3.7038, "España", "Madrid");

        double distance = bsAs.distanceKm(madrid);

        // Distancia aproximada Buenos Aires - Madrid: ~10000 km
        assertThat(distance).isCloseTo(10000.0, offset(1000.0));
    }

    @Test
    void shouldCalculateZeroDistanceSameLocation() {
        Location location = Location.of(-34.6037, -58.3816, "Argentina", "Buenos Aires");
        double distance = location.distanceKm(location);
        assertThat(distance).isCloseTo(0.0, offset(0.1));
    }
}