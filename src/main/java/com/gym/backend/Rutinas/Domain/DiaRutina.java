package com.gym.backend.Rutinas.Domain;

import com.gym.backend.Rutinas.Domain.Enum.DiaSemana;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio para cada día de una rutina
 * Ejemplo: Lunes - "Pecho y Tríceps"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaRutina {
    private Long id;
    private Long rutinaId;
    private DiaSemana diaSemana;
    private String nombre; // "Pecho y Tríceps", "Espalda y Bíceps", "Descanso"
    private String notas;
    private boolean esDescanso; // Si es día de descanso
    private Integer orden;

    @Builder.Default
    private List<EjercicioRutina> ejercicios = new ArrayList<>();
}
