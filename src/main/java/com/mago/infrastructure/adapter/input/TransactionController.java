package com.mago.infrastructure.adapter.input;

import com.mago.application.service.ProcessTransactionUseCase;
import com.mago.domain.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador de entrada REST.
 * <p>
 * Traduce HTTP a objetos del dominio y viceversa.
 * No contiene lógica de negocio.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final ProcessTransactionUseCase useCase;

    public TransactionController(ProcessTransactionUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> processTransaction(
            @RequestBody TransactionRequest request) {

        // Traducir DTO → objetos de dominio
        Transaction transaction = Transaction.create(
                CardNumber.of(request.cardNumber()),
                Money.of(request.amount()),
                Location.of(request.latitude(), request.longitude(),
                        request.country(), request.city()),
                request.merchantName(),
                request.merchantCategory()
        );

        // Ejecutar caso de uso
        List<FraudResult> frauds = useCase.execute(transaction);

        // Traducir resultado → DTO de respuesta
        return ResponseEntity.ok(TransactionResponse.from(transaction, frauds));
    }
}