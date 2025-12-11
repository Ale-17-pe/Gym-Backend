package com.gym.backend.Usuarios.Domain.Enum;

/**
 * Nivel de experiencia del cliente
 */
public enum NivelExperiencia {
    PRINCIPIANTE("Principiante", "Menos de 6 meses entrenando"),
    INTERMEDIO("Intermedio", "6 meses a 2 años entrenando"),
    AVANZADO("Avanzado", "Más de 2 años entrenando"),
    EXPERTO("Experto", "Más de 5 años y conocimiento técnico");

    private final String nombre;
    private final String descripcion;

    NivelExperiencia(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
