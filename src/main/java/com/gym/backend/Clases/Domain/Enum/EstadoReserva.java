package com.gym.backend.Clases.Domain.Enum;

public enum EstadoReserva {
    CONFIRMADA, // Reserva confirmada con cupo
    LISTA_ESPERA, // En lista de espera (clase llena)
    CANCELADA, // Cancelada por el usuario
    ASISTIO, // Usuario asistió a la clase
    NO_ASISTIO // Usuario no asistió (penalización)
}
