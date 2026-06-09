# Motor Antifraude

Sistema de detección de fraudes en tiempo real construido con arquitectura hexagonal.

## Tecnologías
- Java 17
- Spring Boot 3
- Apache Kafka
- PostgreSQL
- Docker
- Arquitectura Hexagonal (Puertos y Adaptadores)
- Event Sourcing

## Estado del proyecto
🟢 En desarrollo - Fase 1: Modelado del dominio

## Estructura del proyecto
- `dominio/` - Lógica de negocio pura, sin frameworks
- `aplicacion/` - Casos de uso y puertos (interfaces)
- `infraestructura/` - Adaptadores (REST, JPA, Kafka)