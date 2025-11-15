package com.gym.backend.Planes.Domain.Exceptions;

public abstract class PlanException extends RuntimeException {
    private final String code;

    public PlanException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() { return code; }
}
