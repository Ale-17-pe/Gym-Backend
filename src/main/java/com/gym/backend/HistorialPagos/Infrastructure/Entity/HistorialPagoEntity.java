package com.gym.backend.HistorialPagos.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad de historial de pagos - NORMALIZADA (3NF)
 * Los campos usuario_id, plan_id, monto se obtienen del pago relacionado.
 */
@Entity
@Table(name = "historial_pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialPagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pagoId;

    // ELIMINADOS por 3NF: usuario_id, plan_id, monto (se obtienen de pagos)

    @Column(length = 50)
    private String estadoAnterior;

    @Column(nullable = false, length = 50)
    private String estadoNuevo;

    @Column(length = 500)
    private String motivoCambio;

    @Column(nullable = false, length = 100)
    private String usuarioModificacion;

    @Column(length = 50)
    private String ipOrigen;

    @Column(nullable = false)
    private LocalDateTime fechaCambio;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (fechaCambio == null) {
            fechaCambio = LocalDateTime.now();
        }
        if (usuarioModificacion == null) {
            usuarioModificacion = "SISTEMA";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}