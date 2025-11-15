package com.gym.backend.Planes.Domain.Exceptions;

public class PlanDuplicateException extends PlanException {
    public PlanDuplicateException(String nombre) {
        super("Ya existe un plan con nombre: " + nombre, "PLAN_DUPLICADO");
    }
}
