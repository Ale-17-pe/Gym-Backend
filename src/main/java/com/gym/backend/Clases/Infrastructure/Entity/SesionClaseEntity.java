package com.gym.backend.Clases.Infrastructure.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gym.backend.Clases.Domain.Enum.EstadoSesion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sesiones_clase", uniqueConstraints = @UniqueConstraint(columnNames = { "horario_clase_id", "fecha" }))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SesionClaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "horario_clase_id", nullable = false)
    @JsonManagedReference
    private HorarioClaseEntity horarioClase;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSesion estado;

    @Column(name = "asistentes_actuales")
    private Integer asistentesActuales;

    @Column(name = "notas_instructor", columnDefinition = "TEXT")
    private String notasInstructor;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoSesion.PROGRAMADA;
        }
        if (asistentesActuales == null) {
            asistentesActuales = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
