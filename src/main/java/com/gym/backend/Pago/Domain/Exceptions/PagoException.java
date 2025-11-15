package com.gym.backend.Pago.Domain.Exceptions;

public abstract class PagoException extends RuntimeException {
    private final String code;
    public PagoException(String message, String code) {
        super(message); this.code = code;
    }
    public String getCode() { return code; }
}