package com.gym.backend.Membresias.Application.Dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CrearMembresiaRequest {
    @NotNull(message = "El ID de usuario es requerido")
    private Long usuarioId;

    @NotNull(message = "El ID de plan es requerido")
    private Long planId;

    @NotNull(message = "El ID de pago es requerido")
    private Long pagoId;

    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es requerida")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDate fechaFin;
}