package com.gym.backend.Asistencias.Application.Dto;

import lombok.Data;

@Data
public class AsistenciaDTO {
    private Long id;
    private Long usuarioId;
    private String fechaHora;
}
