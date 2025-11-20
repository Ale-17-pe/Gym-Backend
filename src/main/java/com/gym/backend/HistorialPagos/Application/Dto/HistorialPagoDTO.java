package com.gym.backend.HistorialPagos.Application.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistorialPagoDTO {
    private Long id;
    private Long pagoId;
    private Long usuarioId;
    private Long planId;
    private Double monto;
    private String estadoAnterior;
    private String estadoNuevo;
    private String motivoCambio;
    private String usuarioModificacion;
    private String ipOrigen;
    private LocalDateTime fechaCambio;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}