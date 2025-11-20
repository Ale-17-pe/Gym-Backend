package com.gym.backend.Asistencias.Application.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaResponse {
    private Long id;
    private Long usuarioId;
    private Long membresiaId;
    private String fecha;
    private String hora;
    private String tipo;
    private String estado;
    private String observaciones;
    private String mensaje; // Para respuestas de error

    public AsistenciaResponse(String mensaje) {
        this.mensaje = mensaje;
    }
}