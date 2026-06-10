package com.mago.infrastructure.adapter.input;

/**
 * DTO para recibir transacciones desde la API REST.
 *
 * @param cardNumber       número de tarjeta
 * @param amount           monto como String (ej: "1500.00")
 * @param latitude         latitud de la ubicación
 * @param longitude        longitud de la ubicación
 * @param country          país
 * @param city             ciudad
 * @param merchantName     nombre del comercio
 * @param merchantCategory categoría del comercio
 */
public record TransactionRequest(
        String cardNumber,
        String amount,
        double latitude,
        double longitude,
        String country,
        String city,
        String merchantName,
        String merchantCategory
) {}