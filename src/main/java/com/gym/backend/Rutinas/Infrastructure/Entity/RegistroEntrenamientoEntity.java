package com.gym.backend.Rutinas.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registro_entrenamiento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEntrenamientoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "rutina_id")
    private Long rutinaId;

    @Column(name = "dia_rutina_id")
    private Long diaRutinaId;

    @Column(name = "fecha_entrenamiento", nullable = false)
    private LocalDateTime fechaEntrenamiento;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(length = 500)
    private String notas;

    @Column(name = "nivel_energia")
    private Integer nivelEnergia;

    @Column(name = "nivel_satisfaccion")
    private Integer nivelSatisfaccion;

    @Column
    private boolean completado = false;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "registroEntrenamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RegistroEjercicioEntity> ejercicios = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (fechaEntrenamiento == null) {
            fechaEntrenamiento = LocalDateTime.now();
        }
    }
}
