package com.gym.backend.Rutinas.Domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio para registrar el progreso de cada ejercicio
 * en una sesión de entrenamiento
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEjercicio {
    private Long id;
    private Long registroEntrenamientoId;
    private Long ejercicioId;
    private String nombreEjercicio;
    private boolean completado;
    private String notas;

    @Builder.Default
    private List<RegistroSerie> series = new ArrayList<>();
}
