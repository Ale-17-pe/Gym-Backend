package com.gym.backend.Rutinas.Domain.Enum;

/**
 * Nivel de dificultad de un ejercicio
 */
public enum Dificultad {
    PRINCIPIANTE("Principiante"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado");

    private final String nombre;

    Dificultad(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
