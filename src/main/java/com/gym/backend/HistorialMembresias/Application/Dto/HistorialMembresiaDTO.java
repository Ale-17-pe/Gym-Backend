package com.gym.backend.HistorialMembresias.Application.Dto;

import lombok.Data;

@Data
public class HistorialMembresiaDTO {

    private Long id;
    private Long usuarioId;
    private Long planId;

    private String fechaInicio;
    private String fechaFin;

    private String accion;
    private String estado;
}