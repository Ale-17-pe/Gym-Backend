package com.gym.backend.Planes.Domain.Exceptions;

public class PlanNotFoundException extends PlanException {
    public PlanNotFoundException(Long id) {
        super("Plan con ID " + id + " no encontrado", "PLAN_NOT_FOUND");
    }
}
