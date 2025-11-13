package com.gym.backend.Planes.Application.Dto;

import lombok.Data;

@Data
public class PlanDTO {

    private Long id;
    private String nombrePlan;
    private String descripcion;
    private Double precio;
    private Integer duracionDias;
    private Boolean activo;
}
