package com.gym.backend.HistorialMembresias.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class HistorialMembresia {

    private Long id;
    private Long usuarioId;
    private Long planId;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private String accion; // CREACION, RENOVACION, VENCIMIENTO
    private String estado; // ACTIVA, VENCIDA, CANCELADA
}
