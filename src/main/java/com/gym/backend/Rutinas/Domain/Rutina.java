package com.gym.backend.Rutinas.Domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio para las rutinas de entrenamiento del cliente
 * El cliente puede tener múltiples rutinas pero solo una activa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rutina {
    private Long id;
    private Long usuarioId;
    private String nombre; // "Rutina de Volumen", "Rutina de Definición"
    private String descripcion;
    private String objetivo; // "Ganar masa muscular", "Perder grasa", etc.
    private Integer duracionSemanas; // Duración planificada
    private boolean activa; // Solo una rutina activa por usuario
    private boolean esPlantilla; // true = plantilla pública creada por admin
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @Builder.Default
    private List<DiaRutina> dias = new ArrayList<>();

    public void activar() {
        this.activa = true;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void desactivar() {
        this.activa = false;
        this.fechaActualizacion = LocalDateTime.now();
    }
}
