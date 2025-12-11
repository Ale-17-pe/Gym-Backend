package com.gym.backend.Fidelidad.Application.Dto;

import com.gym.backend.Fidelidad.Domain.Enum.NivelFidelidad;
import lombok.*;

/**
 * DTO para mostrar el balance de puntos de un usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalancePuntosDTO {
    private Long usuarioId;
    private Integer puntosTotales;
    private Integer puntosDisponibles;
    private Integer puntosCanjeados;
    private NivelFidelidad nivel;
    private String nombreNivel;
    private Double multiplicadorNivel;
    private Integer progresoPorcentaje;
    private Integer puntosParaSiguienteNivel;
    private String siguienteNivel;
}
