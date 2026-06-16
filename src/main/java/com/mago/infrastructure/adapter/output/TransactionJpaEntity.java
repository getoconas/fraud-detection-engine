package com.mago.infrastructure.adapter.output;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Entidad JPA para persistir transacciones en PostgreSQL.
 * <p>
 * Es independiente de la entidad de dominio {@link com.mago.domain.model.Transaction}.
 * Esto permite que el dominio evolucione sin afectar el esquema de base de datos.
 */
@Entity
@Table(name = "transactions")
class TransactionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "card_number", nullable = false, length = 19)
    private String cardNumber;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    @Column(name = "merchant_category", nullable = false)
    private String merchantCategory;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    // Constructor vacío requerido por JPA
    protected TransactionJpaEntity() {}

    private TransactionJpaEntity(UUID id, String cardNumber, BigDecimal amount,
                                 double latitude, double longitude,
                                 String country, String city,
                                 String merchantName, String merchantCategory,
                                 Instant timestamp) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.merchantName = merchantName;
        this.merchantCategory = merchantCategory;
        this.timestamp = timestamp;
    }

    /**
     * Crea una entidad JPA a partir de una transacción del dominio.
     */
    static TransactionJpaEntity fromDomain(com.mago.domain.model.Transaction transaction) {
        return new TransactionJpaEntity(
                transaction.id().value(),
                transaction.cardNumber().value(),
                transaction.amount().amount(),
                transaction.location().latitude(),
                transaction.location().longitude(),
                transaction.location().country(),
                transaction.location().city(),
                transaction.merchantName(),
                transaction.merchantCategory(),
                transaction.timestamp()
        );
    }

    /**
     * Convierte esta entidad JPA a una transacción del dominio.
     */
    com.mago.domain.model.Transaction toDomain() {
        return com.mago.domain.model.Transaction.from(
                com.mago.domain.model.TransactionId.of(id.toString()),
                com.mago.domain.model.CardNumber.of(cardNumber),
                com.mago.domain.model.Money.of(amount),
                com.mago.domain.model.Location.of(latitude, longitude, country, city),
                merchantName,
                merchantCategory,
                timestamp
        );
    }

    // Getters necesarios para JPA (package-private)
    UUID getId() { return id; }
    String getCardNumber() { return cardNumber; }
    BigDecimal getAmount() { return amount; }
    double getLatitude() { return latitude; }
    double getLongitude() { return longitude; }
    String getCountry() { return country; }
    String getCity() { return city; }
    String getMerchantName() { return merchantName; }
    String getMerchantCategory() { return merchantCategory; }
    Instant getTimestamp() { return timestamp; }
}