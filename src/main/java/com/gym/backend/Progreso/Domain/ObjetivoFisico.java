package com.gym.backend.Progreso.Domain;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad de dominio para los objetivos/metas del cliente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjetivoFisico {
    private Long id;
    private Long usuarioId;
    private String nombre; // "Perder 10kg", "Ganar masa muscular", etc.
    private String descripcion;

    // Objetivos de peso
    private Double pesoActual;
    private Double pesoObjetivo;

    // Objetivos de medidas (opcional)
    private Double cinturaObjetivo;
    private Double porcentajeGrasaObjetivo;

    // Fechas
    private LocalDate fechaInicio;
    private LocalDate fechaObjetivo;

    // Estado
    private boolean activo;
    private boolean completado;
    private LocalDate fechaCompletado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    /**
     * Calcula el progreso hacia el objetivo de peso (0-100%)
     */
    public Double calcularProgresoPeso(Double pesoActualHoy) {
        if (pesoActual == null || pesoObjetivo == null || pesoActualHoy == null) {
            return null;
        }

        double diferenciInicial = Math.abs(pesoActual - pesoObjetivo);
        if (diferenciInicial == 0)
            return 100.0;

        double diferenciaActual = Math.abs(pesoActualHoy - pesoObjetivo);
        double progreso = ((diferenciInicial - diferenciaActual) / diferenciInicial) * 100;

        return Math.max(0, Math.min(100, progreso)); // Entre 0 y 100
    }

    /**
     * Calcula los días restantes para el objetivo
     */
    public Long calcularDiasRestantes() {
        if (fechaObjetivo == null)
            return null;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaObjetivo);
    }
}
