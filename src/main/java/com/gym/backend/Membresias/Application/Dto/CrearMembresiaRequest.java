package com.gym.backend.Membresias.Application.Dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CrearMembresiaRequest {
    private Long usuarioId;
    private Long planId;
    private Long pagoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
