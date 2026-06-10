package com.mago.infrastructure.adapter.input;

import com.mago.domain.model.FraudResult;
import com.mago.domain.model.Transaction;

import java.time.Instant;
import java.util.List;

/**
 * DTO para devolver el resultado del procesamiento de una transacción.
 */
public record TransactionResponse(
        String transactionId,
        String status,
        Instant processedAt,
        List<FraudDetail> frauds
) {
    public static TransactionResponse from(Transaction transaction, List<FraudResult> frauds) {
        List<FraudDetail> fraudDetails = frauds.stream()
                .map(f -> new FraudDetail(f.ruleName(), f.reason()))
                .toList();

        return new TransactionResponse(
                transaction.id().value().toString(),
                frauds.isEmpty() ? "APPROVED" : "REJECTED",
                Instant.now(),
                fraudDetails
        );
    }
}

record FraudDetail(String rule, String reason) {}