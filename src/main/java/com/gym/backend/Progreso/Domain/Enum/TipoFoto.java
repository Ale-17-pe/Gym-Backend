package com.gym.backend.Progreso.Domain.Enum;

/**
 * Tipo de foto de progreso
 */
public enum TipoFoto {
    FRENTE("Frente"),
    LATERAL_IZQUIERDO("Lateral Izquierdo"),
    LATERAL_DERECHO("Lateral Derecho"),
    ESPALDA("Espalda"),
    COMPLETO("Cuerpo Completo");

    private final String nombre;

    TipoFoto(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
