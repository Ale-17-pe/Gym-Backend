package com.gym.backend.HistorialMembresias.Domain;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Modelo de dominio para Historial de Membresías - NORMALIZADO (3NF)
 * Los datos de usuario_id, plan_id se obtienen de la membresía relacionada.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class HistorialMembresia {
    private Long id;
    private Long membresiaId;
    // ELIMINADOS por 3NF: usuarioId, planId (se obtienen de membresias)
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