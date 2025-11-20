package com.gym.backend.HistorialPagos.Application.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialPagoResponse {
    private Long id;
    private Long pagoId;
    private Long usuarioId;
    private Long planId;
    private Double monto;
    private String estadoAnterior;
    private String estadoNuevo;
    private String motivoCambio;
    private String usuarioModificacion;
    private String ipOrigen;
    private String fecha;
    private String hora;
    private String tipoCambio;
    private String mensaje;

    public HistorialPagoResponse(String mensaje) {
        this.mensaje = mensaje;
    }
}