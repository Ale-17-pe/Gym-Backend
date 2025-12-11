package com.gym.backend.Usuarios.Domain.Enum;

/**
 * Objetivo fitness del cliente
 */
public enum ObjetivoFitness {
    PERDER_PESO("Perder peso"),
    GANAR_MUSCULO("Ganar masa muscular"),
    MANTENER("Mantenerse en forma"),
    TONIFICAR("Tonificar el cuerpo"),
    MEJORAR_RESISTENCIA("Mejorar resistencia cardiovascular"),
    REHABILITACION("Rehabilitación física"),
    COMPETICION("Preparación para competición");

    private final String descripcion;

    ObjetivoFitness(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
