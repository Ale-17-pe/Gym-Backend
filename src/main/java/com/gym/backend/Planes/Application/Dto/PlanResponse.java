package com.gym.backend.Planes.Application.Dto;

import lombok.Data;

@Data
public class PlanResponse {
    private Long id;
    private String nombrePlan;
    private String descripcion;
    private Double precio;
    private Integer duracionDias;
    private Boolean activo;
    private String beneficios;
    private Double precioMensual; // Campo calculado
}