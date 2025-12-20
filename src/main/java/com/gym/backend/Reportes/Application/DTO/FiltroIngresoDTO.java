package com.gym.backend.Reportes.Application.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroIngresoDTO {
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;
    private String metodoPago;
    private Long planId;
}
