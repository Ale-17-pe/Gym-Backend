package com.gym.backend.Rutinas.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "registro_serie")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroSerieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registro_ejercicio_id", nullable = false)
    private RegistroEjercicioEntity registroEjercicio;

    @Column(name = "numero_serie", nullable = false)
    private Integer numeroSerie;

    @Column(name = "peso_kg")
    private Double pesoKg;

    @Column(name = "repeticiones_logradas")
    private Integer repeticionesLogradas;

    @Column(name = "repeticiones_objetivo")
    private Integer repeticionesObjetivo;

    @Column
    private boolean completada = false;

    @Column(length = 255)
    private String notas;
}
