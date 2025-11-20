package com.gym.backend.HistorialMembresias.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_membresias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialMembresiaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long membresiaId;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Long planId;

    @Column(nullable = false, length = 50)
    private String accion;

    @Column(length = 50)
    private String estadoAnterior;

    @Column(length = 50)
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