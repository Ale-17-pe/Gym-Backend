package com.gym.backend.Rutinas.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registro_ejercicio")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEjercicioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registro_entrenamiento_id", nullable = false)
    private RegistroEntrenamientoEntity registroEntrenamiento;

    @Column(name = "ejercicio_id", nullable = false)
    private Long ejercicioId;

    @Column(name = "nombre_ejercicio", length = 100)
    private String nombreEjercicio;

    @Column
    private boolean completado = false;

    @Column(length = 255)
    private String notas;

    @OneToMany(mappedBy = "registroEjercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numeroSerie ASC")
    @Builder.Default
    private List<RegistroSerieEntity> series = new ArrayList<>();
}
