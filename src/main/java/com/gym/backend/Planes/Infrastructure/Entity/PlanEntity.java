package com.gym.backend.Planes.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "planes", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nombrePlan", name = "uk_plan_nombre")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombrePlan;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer duracionDias;

    @Column(nullable = false)
    private Boolean activo;

    @Column(length = 1000)
    private String beneficios;

    // Nuevos campos para analytics
    @Column(nullable = false)
    @Builder.Default
    private Integer vecesContratado = 0;

    @Column(nullable = false)
    @Builder.Default
    private Double ratingPromedio = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean destacado = false;

    @Column(length = 50)
    private String categoria; // BASIC, PREMIUM, VIP, etc.

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
        if (vecesContratado == null) {
            vecesContratado = 0;
        }
        if (ratingPromedio == null) {
            ratingPromedio = 0.0;
        }
        if (destacado == null) {
            destacado = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Método para incrementar contrataciones
    public void incrementarContrataciones() {
        this.vecesContratado++;
    }
}