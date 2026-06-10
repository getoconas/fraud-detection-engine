package com.mago.infrastructure.adapter.output;

import com.mago.application.port.TransactionRepository;
import com.mago.domain.model.Transaction;
import com.mago.domain.model.TransactionId;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria del repositorio de transacciones.
 * <p>
 * Para desarrollo y tests. Se reemplazará por JPA/PostgreSQL.
 */
public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<TransactionId, Transaction> store = new ConcurrentHashMap<>();

    @Override
    public void save(Transaction transaction) {
        store.put(transaction.id(), transaction);
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return Optional.ofNullable(store.get(id));
    }
}