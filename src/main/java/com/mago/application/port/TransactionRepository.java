package com.mago.application.port;

import com.mago.domain.model.Transaction;
import com.mago.domain.model.TransactionId;

import java.util.Optional;

/**
 * Puerto de salida para persistencia de transacciones.
 * <p>
 * El dominio define qué necesita, la infraestructura decide cómo:
 * - En memoria (para tests)
 * - PostgreSQL (producción)
 * - MongoDB (alternativa)
 */
public interface TransactionRepository {

    void save(Transaction transaction);

    Optional<Transaction> findById(TransactionId id);
}