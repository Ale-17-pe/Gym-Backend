package com.gym.backend.Pago.Domain.Exceptions;

public class PagoNotFoundException extends PagoException {
    public PagoNotFoundException(Long id) {
        super("Pago con ID " + id + " no encontrado", "PAGO_NOT_FOUND");
    }
}