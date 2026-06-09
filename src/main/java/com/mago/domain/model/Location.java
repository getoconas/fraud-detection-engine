package com.mago.domain.model;

import java.util.Objects;

/**
 * Value Object que representa una ubicación geográfica.
 * <p>
 * Características:
 * <ul>
 *   <li>Inmutable: una vez creada, no puede modificarse</li>
 *   <li>Coordenadas geográficas: latitud (-90 a 90) y longitud (-180 a 180)</li>
 *   <li>País y ciudad: normalizados a mayúsculas para comparaciones consistentes</li>
 *   <li>Cálculo de distancia: fórmula de Haversine en kilómetros</li>
 *   <li>Comparación por valor: dos ubicaciones con los mismos datos son iguales</li>
 * </ul>
 * <p>
 * La fórmula de Haversine calcula la distancia más corta entre dos puntos
 * sobre la superficie terrestre, asumiendo la Tierra como una esfera de 6371 km de radio.
 *
 * @author mago
 */
public final class Location {
    private final double latitude;
    private final double longitude;
    private final String country;
    private final String city;

    /**
     * Constructor privado. Usar el método factory {@link #of}.
     */
    private Location(double latitude, double longitude, String country, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
    }

    /**
     * Crea una Location con todos los datos requeridos.
     * <p>
     * Valida:
     * <ul>
     *   <li>Latitud entre -90 y 90 grados</li>
     *   <li>Longitud entre -180 y 180 grados</li>
     *   <li>País y ciudad no pueden ser nulos ni vacíos</li>
     * </ul>
     * Normaliza país a mayúsculas para comparaciones consistentes.
     *
     * @param latitude  latitud en grados decimales
     * @param longitude longitud en grados decimales
     * @param country   nombre del país
     * @param city      nombre de la ciudad
     * @return una nueva instancia de Location
     * @throws IllegalArgumentException si algún dato no es válido
     */
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

    /** @return latitud en grados decimales */
    public double latitude() { return latitude; }

    /** @return longitud en grados decimales */
    public double longitude() { return longitude; }

    /** @return país normalizado a mayúsculas */
    public String country() { return country; }

    /** @return ciudad con formato original */
    public String city() { return city; }

    /**
     * Determina si esta ubicación está en el mismo país que otra.
     * <p>
     * La comparación es case-insensitive porque el país se normaliza
     * a mayúsculas al crear la instancia.
     *
     * @param other la otra ubicación a comparar
     * @return true si ambas ubicaciones están en el mismo país
     */
    public boolean isSameCountry(Location other) {
        return this.country.equals(other.country);
    }

    /**
     * Calcula la distancia en kilómetros entre esta ubicación y otra.
     * <p>
     * Utiliza la fórmula de Haversine, que considera la curvatura terrestre.
     * Asume la Tierra como una esfera perfecta de radio 6371 km.
     * <p>
     * Precisión típica: error menor al 0.5% para la mayoría de los casos.
     * No apto para cálculos que requieran precisión centimétrica.
     *
     * @param other la ubicación de destino
     * @return distancia en kilómetros entre ambas ubicaciones
     */
    public double distanceKm(Location other) {
        double lat1 = Math.toRadians(this.latitude);
        double lat2 = Math.toRadians(other.latitude);
        double dLat = Math.toRadians(other.latitude - this.latitude);
        double dLon = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371.0 * c;
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