package com.gym.backend.Pago.Domain.Exceptions;

public class PagoValidationException extends PagoException {
    public PagoValidationException(String message) {
        super(message, "PAGO_VALIDACION");
    }
}