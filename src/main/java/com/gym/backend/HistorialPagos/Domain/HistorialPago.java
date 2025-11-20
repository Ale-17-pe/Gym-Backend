package com.gym.backend.HistorialPagos.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class HistorialPago {
    private Long id;
    private Long pagoId;
    private Long usuarioId;
    private Long planId; // Nueva relación con plan
    private Double monto;
    private String estadoAnterior;
    private String estadoNuevo;
    private String motivoCambio; // Nuevo campo
    private String usuarioModificacion; // Quién realizó el cambio
    private String ipOrigen; // Para auditoría
    private LocalDateTime fechaCambio;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Métodos de negocio
    public boolean esCambioDeEstado() {
        return estadoAnterior != null && estadoNuevo != null && !estadoAnterior.equals(estadoNuevo);
    }

    public boolean esConfirmacion() {
        return "PENDIENTE".equals(estadoAnterior) && "CONFIRMADO".equals(estadoNuevo);
    }

    public boolean esRechazo() {
        return "PENDIENTE".equals(estadoAnterior) && "RECHAZADO".equals(estadoNuevo);
    }

    public String obtenerTipoCambio() {
        if (esConfirmacion()) return "CONFIRMACION";
        if (esRechazo()) return "RECHAZO";
        if (esCambioDeEstado()) return "CAMBIO_ESTADO";
        return "OTRO";
    }
}