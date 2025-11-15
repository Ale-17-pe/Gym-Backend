package com.gym.backend.Planes.Application.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CrearPlanRequest {
    @NotBlank(message = "El nombre del plan es requerido")
    private String nombrePlan;

    @NotBlank(message = "La descripción es requerida")
    private String descripcion;

    @NotNull(message = "El precio es requerido")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotNull(message = "La duración es requerida")
    @Positive(message = "La duración debe ser mayor a 0")
    private Integer duracionDias;

    private String beneficios;
}
