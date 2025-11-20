package com.gym.backend.Planes.Application.Dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ActualizarPlanRequest {
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombrePlan;

    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String descripcion;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "10000.0", message = "El precio no puede exceder $10,000")
    private Double precio;

    @Min(value = 1, message = "La duración mínima es 1 día")
    @Max(value = 365, message = "La duración máxima es 365 días")
    private Integer duracionDias;

    @Size(max = 1000, message = "Los beneficios no pueden exceder 1000 caracteres")
    private String beneficios;

    private Boolean activo;
    private Boolean destacado;

    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String categoria;
}