package com.gym.backend.Rutinas.Domain;

import lombok.*;

/**
 * Entidad de dominio para registrar cada serie de un ejercicio
 * Con peso y repeticiones reales logradas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroSerie {
    private Long id;
    private Long registroEjercicioId;
    private Integer numeroSerie;
    private Double pesoKg;
    private Integer repeticionesLogradas;
    private Integer repeticionesObjetivo;
    private boolean completada;
    private String notas; // "Fallé en la última rep", etc.
}
