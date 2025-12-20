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
public class IngresoDetalladoDTO {
    private LocalDateTime fechaPago;
    private String usuarioDni;
    private String usuarioNombre;
    private String planNombre;
    private Double monto;
    private String metodoPago;
    private String estado;
    private String codigoPago;
    private String observacion;
}
