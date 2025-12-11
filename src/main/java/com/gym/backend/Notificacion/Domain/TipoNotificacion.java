package com.gym.backend.Notificacion.Domain;

public enum TipoNotificacion {
    MEMBRESIA_PROXIMA_VENCER("Tu membresía está próxima a vencer"),
    MEMBRESIA_VENCIDA("Tu membresía ha vencido"),
    PAGO_CONFIRMADO("Pago confirmado exitosamente"),
    PAGO_PENDIENTE("Tienes un pago pendiente"),
    BIENVENIDA("¡Bienvenido a AresFitness!"),
    CUMPLEANOS("¡Feliz cumpleaños!"),
    GENERAL("Notificación general");

    private final String descripcion;

    TipoNotificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
