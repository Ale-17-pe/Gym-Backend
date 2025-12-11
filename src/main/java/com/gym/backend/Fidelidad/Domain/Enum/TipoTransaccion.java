package com.gym.backend.Fidelidad.Domain.Enum;

/**
 * Tipos de transacciones de puntos
 */
public enum TipoTransaccion {
    GANANCIA, // Puntos ganados
    CANJE, // Puntos canjeados por recompensa
    EXPIRACION, // Puntos expirados
    AJUSTE // Ajuste manual por admin
}
