# Modelo Inicial del Dominio - Detección de Fraudes

## Datos de una Transacción
- Monto: BigDecimal (ej: 1500.00)
- Ubicación: latitud, longitud, país, ciudad
- Tarjeta: número enmascarado, ID único de tarjeta
- Fecha y hora: Instant (UTC)
- Comercio: nombre, categoría (minorista, viajes, comida, tecnología)

## Datos del Historial del Cliente
- Monto promedio de transacciones (últimos 30 días)
- Países/ciudades frecuentes visitados
- Última transacción: fecha, hora y ubicación
- Cantidad total de transacciones en últimas 24 horas

## Reglas Iniciales de Fraude
- Regla 1: Monto 5 veces mayor al promedio de los últimos 30 días
- Regla 2: Dos transacciones en países distintos en menos de 1 hora (viaje imposible)
- Regla 3: Más de 10 transacciones en 10 minutos (verificación de velocidad)
- Regla 4: Transacción en un país nunca antes visitado + monto alto
- Regla 5: Transacción en horario inusual (hora local 2am-5am) + monto alto