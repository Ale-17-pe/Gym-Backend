package com.gym.backend.Usuarios.Domain.Enum;

/**
 * Turno de trabajo para empleados
 */
public enum Turno {
    MANANA("Mañana", "06:00 - 14:00"),
    TARDE("Tarde", "14:00 - 22:00"),
    NOCHE("Noche", "22:00 - 06:00"),
    ROTATIVO("Rotativo", "Horario variable");

    private final String nombre;
    private final String horario;

    Turno(String nombre, String horario) {
        this.nombre = nombre;
        this.horario = horario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getHorario() {
        return horario;
    }
}
