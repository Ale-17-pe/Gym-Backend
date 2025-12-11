package com.gym.backend.Progreso.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "objetivo_fisico")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjetivoFisicoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "peso_actual")
    private Double pesoActual;

    @Column(name = "peso_objetivo")
    private Double pesoObjetivo;

    @Column(name = "cintura_objetivo")
    private Double cinturaObjetivo;

    @Column(name = "porcentaje_grasa_objetivo")
    private Double porcentajeGrasaObjetivo;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_objetivo")
    private LocalDate fechaObjetivo;

    @Column
    private boolean activo = true;

    @Column
    private boolean completado = false;

    @Column(name = "fecha_completado")
    private LocalDate fechaCompletado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
