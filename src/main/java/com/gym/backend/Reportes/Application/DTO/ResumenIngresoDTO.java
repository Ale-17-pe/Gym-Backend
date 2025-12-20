package com.gym.backend.Reportes.Application.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenIngresoDTO {
    private Double totalConfirmado;
    private Double totalPendiente;
    private Double totalCancelado;
    private Double totalGeneral;
    private Integer cantidadTransacciones;
    private Integer cantidadConfirmadas;
    private Integer cantidadPendientes;
    private Integer cantidadCanceladas;
}
