package com.gym.backend.Rutinas.Domain.Enum;

/**
 * Grupos musculares para categorizar ejercicios
 */
public enum GrupoMuscular {
    PECHO("Pecho"),
    ESPALDA("Espalda"),
    HOMBROS("Hombros"),
    BICEPS("Bíceps"),
    TRICEPS("Tríceps"),
    ANTEBRAZOS("Antebrazos"),
    CUADRICEPS("Cuádriceps"),
    ISQUIOTIBIALES("Isquiotibiales"),
    GLUTEOS("Glúteos"),
    PANTORRILLAS("Pantorrillas"),
    ABDOMINALES("Abdominales"),
    OBLICUOS("Oblicuos"),
    TRAPECIO("Trapecio"),
    LUMBARES("Lumbares"),
    CARDIO("Cardio"),
    CUERPO_COMPLETO("Cuerpo Completo");

    private final String nombre;

    GrupoMuscular(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
