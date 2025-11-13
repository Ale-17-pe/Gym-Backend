package com.gym.backend.HistorialPagos.Application.Dto;

import lombok.Data;

@Data
public class HistorialPagoDTO {

    private Long id;
    private Long pagoId;
    private Long usuarioId;
    private Double monto;

    private String estadoAnterior;
    private String estadoNuevo;

    private String fechaCambio;
}