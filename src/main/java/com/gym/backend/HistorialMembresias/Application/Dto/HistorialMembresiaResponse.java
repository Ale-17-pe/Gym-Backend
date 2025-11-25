package com.gym.backend.HistorialMembresias.Application.Dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialMembresiaResponse {
    private Long id;
    private Long membresiaId;
    private Long usuarioId;
    private Long planId;
    private String accion;
    private String estadoAnterior;
    private String estadoNuevo;
    private String motivoCambio;
    private String usuarioModificacion;
    private String ipOrigen;
    private String fecha;
    private String hora;
    private String tipoCambio;
    private String mensaje;

    public HistorialMembresiaResponse(String mensaje) {
        this.mensaje = mensaje;
    }
}