package com.gym.backend.Membresias.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class Membresia {

    private Long id;
    private Long usuarioId;
    private Long planId;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private String estado; // ACTIVA / VENCIDA
}
