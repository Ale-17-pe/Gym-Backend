package com.gym.backend.Fidelidad.Domain.Enum;

/**
 * Estados de un canje de puntos
 */
public enum EstadoCanje {
    PENDIENTE, // Canje solicitado, pendiente de entrega
    COMPLETADO, // Canje entregado/utilizado
    CANCELADO, // Canje cancelado (puntos devueltos)
    EXPIRADO // Canje expirado sin usar
}
