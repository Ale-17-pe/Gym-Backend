package com.gym.backend.Usuarios.Infrastructure.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gym.backend.Usuarios.Domain.Enum.EspecialidadEntrenador;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA para datos específicos de ENTRENADORES
 * Relación 1:1 con Empleado
 */
@Entity
@Table(name = "entrenador")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrenadorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empleado_id", unique = true, nullable = false)
    @JsonIgnoreProperties({ "entrenador", "hibernateLazyInitializer", "handler" })
    private EmpleadoEntity empleado;

    // Campo directo para consultas rápidas
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private EspecialidadEntrenador especialidad;

    @Column(columnDefinition = "TEXT")
    private String certificaciones;

    @Column(name = "experiencia_anios")
    private Integer experienciaAnios;

    @Column(name = "max_clientes")
    private Integer maxClientes;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    @Column(name = "rating_promedio")
    private Double ratingPromedio;

    @Column(name = "total_resenas")
    @Builder.Default
    private Integer totalResenas = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
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
