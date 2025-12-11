package com.gym.backend.Rutinas.Domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio para registrar cada sesión de entrenamiento
 * Permite al cliente ver su progreso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEntrenamiento {
    private Long id;
    private Long usuarioId;
    private Long rutinaId;
    private Long diaRutinaId;
    private LocalDateTime fechaEntrenamiento;
    private Integer duracionMinutos;
    private String notas; // Notas del cliente sobre la sesión
    private Integer nivelEnergia; // 1-5
    private Integer nivelSatisfaccion; // 1-5
    private boolean completado;
    private LocalDateTime fechaCreacion;

    @Builder.Default
    private List<RegistroEjercicio> ejercicios = new ArrayList<>();
}
