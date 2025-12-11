package com.gym.backend.Usuarios.Domain.Enum;

/**
 * Especialidad del entrenador
 */
public enum EspecialidadEntrenador {
    MUSCULACION("Musculación", "Entrenamiento de fuerza e hipertrofia"),
    CARDIO("Cardio", "Entrenamiento cardiovascular"),
    CROSSFIT("CrossFit", "Entrenamiento funcional de alta intensidad"),
    YOGA("Yoga", "Yoga y meditación"),
    PILATES("Pilates", "Pilates y control corporal"),
    FUNCIONAL("Funcional", "Entrenamiento funcional"),
    SPINNING("Spinning", "Ciclismo indoor"),
    NATACION("Natación", "Entrenamiento en piscina"),
    ARTES_MARCIALES("Artes Marciales", "Boxeo, MMA, Karate, etc."),
    NUTRICION("Nutrición", "Asesoría nutricional");

    private final String nombre;
    private final String descripcion;

    EspecialidadEntrenador(String nombre, String descripcion) {
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
