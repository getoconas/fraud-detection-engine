package com.mago.application.service;

import com.mago.domain.model.CardNumber;
import com.mago.domain.model.CustomerHistory;
import com.mago.domain.model.Transaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de aplicación para gestionar el historial de clientes.
 * <p>
 * En esta fase usa un mapa en memoria.
 * En el futuro se reemplazará con persistencia real (PostgreSQL + Kafka Streams).
 */
public class CustomerHistoryService {

    private final Map<CardNumber, CustomerHistory> historyStore = new ConcurrentHashMap<>();

    public CustomerHistory getHistory(CardNumber cardNumber) {
        return historyStore.getOrDefault(cardNumber, CustomerHistory.empty(cardNumber));
    }

    public void updateHistory(Transaction transaction, CustomerHistory currentHistory) {
        CustomerHistory updated = currentHistory.updateWith(transaction);
        historyStore.put(transaction.cardNumber(), updated);
    }
}