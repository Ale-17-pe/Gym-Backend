package com.gym.backend.Rutinas.Infrastructure.Entity;

import com.gym.backend.Rutinas.Domain.Enum.DiaSemana;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dia_rutina")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaRutinaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rutina_id", nullable = false)
    private RutinaEntity rutina;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DiaSemana diaSemana;

    @Column(length = 100)
    private String nombre;

    @Column(length = 255)
    private String notas;

    @Column(name = "es_descanso")
    private boolean esDescanso = false;

    @Column
    private Integer orden;

    @OneToMany(mappedBy = "diaRutina", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    @Builder.Default
    private List<EjercicioRutinaEntity> ejercicios = new ArrayList<>();
}
