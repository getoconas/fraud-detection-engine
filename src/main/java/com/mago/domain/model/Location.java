package com.mago.domain.model;

import java.util.Objects;

/**
 * Value Object que representa una ubicación geográfica.
 * Inmutable, con latitud, longitud, país y ciudad.
 */
public final class Location {
    private final double latitude;
    private final double longitude;
    private final String country;
    private final String city;

    private Location(double latitude, double longitude, String country, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
    }

    public static Location of(double latitude, double longitude, String country, String city) {
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be null or blank");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City cannot be null or blank");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude: " + latitude);
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude: " + longitude);
        }
        return new Location(latitude, longitude, country.trim().toUpperCase(), city.trim());
    }

    public double latitude() { return latitude; }
    public double longitude() { return longitude; }
    public String country() { return country; }
    public String city() { return city; }

    public boolean isSameCountry(Location other) {
        return this.country.equals(other.country);
    }

    public double distanceKm(Location other) {
        // Fórmula de Haversine para distancia entre dos puntos geográficos
        double lat1 = Math.toRadians(this.latitude);
        double lat2 = Math.toRadians(other.latitude);
        double dLat = Math.toRadians(other.latitude - this.latitude);
        double dLon = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371.0 * c; // Radio de la Tierra en km
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(latitude, location.latitude) == 0
                && Double.compare(longitude, location.longitude) == 0
                && country.equals(location.country)
                && city.equals(location.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, country, city);
    }

    @Override
    public String toString() {
        return city + ", " + country;
    }
}