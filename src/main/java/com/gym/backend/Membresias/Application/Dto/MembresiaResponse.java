package com.gym.backend.Membresias.Application.Dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MembresiaResponse {
    private Long id;
    private Long usuarioId;
    private Long planId;
    private Long pagoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private boolean activa;
    private long diasRestantes;
    private String nombrePlan;
    private String nombreUsuario;
    private LocalDate fechaCreacion;
    private LocalDate fechaActualizacion;
}