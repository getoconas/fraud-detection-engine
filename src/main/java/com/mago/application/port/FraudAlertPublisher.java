package com.mago.application.port;

import com.mago.domain.model.FraudResult;

import java.util.List;

/**
 * Puerto de salida para publicar alertas de fraude.
 * <p>
 * El dominio define qué publicar, la infraestructura decide cómo:
 * - Imprimir en consola (desarrollo)
 * - Publicar a Kafka (producción)
 * - Enviar email/SMS (futuro)
 */
public interface FraudAlertPublisher {

    void publish(List<FraudResult> frauds);
}