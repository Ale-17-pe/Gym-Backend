package com.gym.backend.Rutinas.Domain;

import com.gym.backend.Rutinas.Domain.Enum.Dificultad;
import com.gym.backend.Rutinas.Domain.Enum.GrupoMuscular;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad de dominio para el catálogo de ejercicios
 * Los administradores crean los ejercicios y los clientes los seleccionan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ejercicio {
    private Long id;
    private String nombre;
    private String descripcion;
    private GrupoMuscular grupoMuscular;
    private GrupoMuscular grupoMuscularSecundario; // Opcional
    private Dificultad dificultad;
    private String equipamiento; // "Barra", "Mancuernas", "Máquina", "Peso corporal"
    private String instrucciones;
    private String imagenUrl;
    private String videoUrl;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
