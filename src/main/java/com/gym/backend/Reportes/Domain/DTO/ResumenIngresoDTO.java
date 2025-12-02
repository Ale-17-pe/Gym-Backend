package com.gym.backend.Reportes.Domain.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
