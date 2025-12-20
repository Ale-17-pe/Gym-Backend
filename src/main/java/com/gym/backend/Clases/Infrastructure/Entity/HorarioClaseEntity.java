package com.gym.backend.Clases.Infrastructure.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gym.backend.Usuarios.Infrastructure.Entity.EntrenadorEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "horarios_clase")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HorarioClaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_clase_id", nullable = false)
    private TipoClaseEntity tipoClase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entrenador_id", nullable = false)
    @JsonIgnoreProperties({ "empleado", "hibernateLazyInitializer", "handler" })
    private EntrenadorEntity entrenador;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana; // 1=Lunes, 7=Domingo

    @Column(name = "hora_inicio", nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @Column(name = "aforo_maximo", nullable = false)
    private Integer aforoMaximo;

    @Column(length = 100)
    private String sala;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
