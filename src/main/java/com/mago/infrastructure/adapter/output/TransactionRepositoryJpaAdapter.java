package com.mago.infrastructure.adapter.output;

import com.mago.application.port.TransactionRepository;
import com.mago.domain.model.Transaction;
import com.mago.domain.model.TransactionId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Adaptador JPA que implementa el puerto {@link TransactionRepository}.
 * <p>
 * Traduce entre la entidad de dominio y la entidad JPA,
 * manteniendo el dominio completamente aislado de la infraestructura.
 */
@Component
class TransactionRepositoryJpaAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;

    TransactionRepositoryJpaAdapter(TransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public void save(Transaction transaction) {
        TransactionJpaEntity entity = TransactionJpaEntity.fromDomain(transaction);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return jpaRepository.findById(id.value())
                .map(TransactionJpaEntity::toDomain);
    }
}