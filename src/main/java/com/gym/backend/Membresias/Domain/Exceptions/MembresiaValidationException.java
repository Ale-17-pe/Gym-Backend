package com.gym.backend.Membresias.Domain.Exceptions;

public class MembresiaValidationException extends MembresiaException {
    public MembresiaValidationException(String message) {
        super(message, "MEMBRESIA_VALIDACION");
    }
}