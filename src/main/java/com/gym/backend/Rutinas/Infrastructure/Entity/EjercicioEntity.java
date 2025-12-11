package com.gym.backend.Rutinas.Infrastructure.Entity;

import com.gym.backend.Rutinas.Domain.Enum.Dificultad;
import com.gym.backend.Rutinas.Domain.Enum.GrupoMuscular;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ejercicio")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EjercicioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "grupo_muscular", nullable = false)
    private GrupoMuscular grupoMuscular;

    @Enumerated(EnumType.STRING)
    @Column(name = "grupo_muscular_secundario")
    private GrupoMuscular grupoMuscularSecundario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Dificultad dificultad;

    @Column(length = 100)
    private String equipamiento;

    @Column(columnDefinition = "TEXT")
    private String instrucciones;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Column(name = "video_url", length = 255)
    private String videoUrl;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creacion")
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
