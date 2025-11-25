package com.gym.backend.HistorialMembresias.Domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class HistorialMembresia {
    private Long id;
    private Long membresiaId;
    private Long usuarioId;
    private Long planId;
    private String accion; // CREAR, EXTENDER, SUSPENDER, REACTIVAR, CANCELAR, VENCER
    private String estadoAnterior;
    private String estadoNuevo;
    private String motivoCambio;
    private String usuarioModificacion;
    private String ipOrigen;
    private LocalDateTime fechaCambio;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Métodos de negocio
    public boolean esCreacion() {
        return "CREAR".equals(accion);
    }

    public boolean esExtension() {
        return "EXTENDER".equals(accion);
    }

    public boolean esSuspension() {
        return "SUSPENDER".equals(accion);
    }

    public boolean esReactivacion() {
        return "REACTIVAR".equals(accion);
    }

    public boolean esCancelacion() {
        return "CANCELAR".equals(accion);
    }

    public boolean esVencimiento() {
        return "VENCER".equals(accion);
    }

    public String obtenerTipoCambio() {
        return accion;
    }
}