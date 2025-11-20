package com.gym.backend.Planes.Application.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlanResponse {
    private Long id;
    private String nombrePlan;
    private String descripcion;
    private Double precio;
    private Integer duracionDias;
    private Boolean activo;
    private String beneficios;
    private Double precioMensual;

    private Integer vecesContratado;
    private Double ratingPromedio;
    private Boolean destacado;
    private String categoria;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}