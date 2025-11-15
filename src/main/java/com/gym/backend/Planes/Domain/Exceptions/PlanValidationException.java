package com.gym.backend.Planes.Domain.Exceptions;

public class PlanValidationException extends PlanException {
    public PlanValidationException(String message) {
        super(message, "PLAN_VALIDACION");
    }
}