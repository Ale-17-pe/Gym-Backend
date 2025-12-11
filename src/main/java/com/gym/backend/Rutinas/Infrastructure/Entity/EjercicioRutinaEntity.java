package com.gym.backend.Rutinas.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ejercicio_rutina")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EjercicioRutinaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dia_rutina_id", nullable = false)
    private DiaRutinaEntity diaRutina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejercicio_id", nullable = false)
    private EjercicioEntity ejercicio;

    @Column(nullable = false)
    private Integer series;

    @Column
    private Integer repeticiones;

    @Column(name = "rango_repeticiones", length = 20)
    private String rangoRepeticiones;

    @Column(name = "descanso_segundos")
    private Integer descansoSegundos;

    @Column(name = "peso_sugerido")
    private Double pesoSugerido;

    @Column
    private Integer orden;

    @Column(length = 255)
    private String notas;

    @Column(length = 20)
    private String tempo;
}
