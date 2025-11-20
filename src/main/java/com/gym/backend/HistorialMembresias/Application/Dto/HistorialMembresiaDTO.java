package com.gym.backend.HistorialMembresias.Application.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistorialMembresiaDTO {
    private Long id;
    private Long membresiaId;
    private Long usuarioId;
    private Long planId;
    private String accion;
    private String estadoAnterior;
    private String estadoNuevo;
    private String motivoCambio;
    private String usuarioModificacion;
    private String ipOrigen;
    private LocalDateTime fechaCambio;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}