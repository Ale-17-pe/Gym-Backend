package com.gym.backend.Fidelidad.Infrastructure.Entity;

import com.gym.backend.Fidelidad.Domain.Enum.NivelFidelidad;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA para el balance de puntos de fidelidad
 */
@Entity
@Table(name = "puntos_fidelidad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuntosFidelidadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false, unique = true)
    private Long usuarioId;

    @Column(name = "puntos_totales", nullable = false)
    @Builder.Default
    private Integer puntosTotales = 0;

    @Column(name = "puntos_disponibles", nullable = false)
    @Builder.Default
    private Integer puntosDisponibles = 0;

    @Column(name = "puntos_canjeados", nullable = false)
    @Builder.Default
    private Integer puntosCanjeados = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel", nullable = false, length = 20)
    @Builder.Default
    private NivelFidelidad nivel = NivelFidelidad.BRONCE;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
