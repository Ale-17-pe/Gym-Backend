package com.gym.backend.Planes.Domain;

import com.gym.backend.Planes.Domain.Exceptions.PlanValidationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
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
}