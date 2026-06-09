# Motor Antifraude

Sistema de detección de fraudes en tiempo real construido con arquitectura hexagonal.

## Tecnologías
- Java 22
- Spring Boot 3
- Apache Kafka
- PostgreSQL
- Docker
- Arquitectura Hexagonal (Puertos y Adaptadores)
- Event Sourcing

## Estado del proyecto
🟢 En desarrollo - Fase 1: Modelado del dominio

## Estructura del proyecto
- `domain/` - Lógica de negocio pura, sin frameworks
- `application/` - Casos de uso y puertos (interfaces)
- `infrastructure/` - Adaptadores (REST, JPA, Kafka)