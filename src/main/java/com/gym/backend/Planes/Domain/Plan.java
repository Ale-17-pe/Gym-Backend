package com.gym.backend.Planes.Domain;

import com.gym.backend.Planes.Domain.Exceptions.PlanValidationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Plan {
    private Long id;
    private String nombrePlan;
    private String descripcion;
    private Double precio;
    private Integer duracionDias;
    private Boolean activo;
    private String beneficios;

    // Campos de analytics que deben existir
    private Integer vecesContratado;
    private Double ratingPromedio;
    private Boolean destacado;
    private String categoria;

    // Campos de auditoría que deben existir
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public void validar() {
        if (nombrePlan == null || nombrePlan.trim().isEmpty()) {
            throw new PlanValidationException("El nombre del plan es requerido");
        }
        if (precio == null || precio <= 0) {
            throw new PlanValidationException("El precio debe ser mayor a 0");
        }
        if (duracionDias == null || duracionDias <= 0) {
            throw new PlanValidationException("La duración en días debe ser mayor a 0");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new PlanValidationException("La descripción es requerida");
        }
        if (categoria != null && categoria.length() > 50) {
            throw new PlanValidationException("La categoría no puede exceder 50 caracteres");
        }
    }

    public boolean esActivo() {
        return activo != null && activo;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public Double calcularPrecioMensual() {
        if (duracionDias == null || duracionDias == 0) return precio;
        return (precio / duracionDias) * 30; // Precio mensual aproximado
    }

    public void incrementarContrataciones() {
        this.vecesContratado = (this.vecesContratado == null) ? 1 : this.vecesContratado + 1;
    }

    public void actualizarRating(Double nuevoRating) {
        if (this.ratingPromedio == null) {
            this.ratingPromedio = nuevoRating;
        } else {
            this.ratingPromedio = (this.ratingPromedio + nuevoRating) / 2;
        }
    }
}