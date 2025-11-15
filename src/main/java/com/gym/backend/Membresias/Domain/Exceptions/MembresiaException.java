package com.gym.backend.Membresias.Domain.Exceptions;

public abstract class MembresiaException extends RuntimeException {
    private final String code;
    public MembresiaException(String message, String code) {
        super(message); this.code = code;
    }
    public String getCode() { return code; }
}