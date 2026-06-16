package com.mago.infrastructure.adapter.output;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositorio Spring Data JPA para transacciones.
 * <p>
 * Spring genera automáticamente las queries básicas (findById, save, etc.).
 */
@Repository
interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, UUID> {
}