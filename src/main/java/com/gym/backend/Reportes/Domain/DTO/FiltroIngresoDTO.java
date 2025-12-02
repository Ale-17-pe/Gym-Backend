package com.gym.backend.Reportes.Domain.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FiltroIngresoDTO {
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado; // CONFIRMADO, PENDIENTE, CANCELADO
    private String metodoPago; // YAPE, PLIN, EFECTIVO, etc.
    private Long planId; // ID del plan (opcional)
}
