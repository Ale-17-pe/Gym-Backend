package com.gym.backend.Asistencias.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "asistencias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsistenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Long membresiaId;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 20)
    private String tipo; // ENTRADA, SALIDA

    @Column(nullable = false, length = 20)
    private String estado; // REGISTRADA, CANCELADA

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (estado == null) {
            estado = "REGISTRADA";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}