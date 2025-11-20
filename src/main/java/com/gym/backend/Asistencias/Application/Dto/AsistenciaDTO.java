package com.gym.backend.Asistencias.Application.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AsistenciaDTO {
    private Long id;
    private Long usuarioId;
    private Long membresiaId;
    private LocalDateTime fechaHora;
    private String tipo;
    private String estado;
    private String observaciones;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}