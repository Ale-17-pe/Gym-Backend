package com.gym.backend.Comprobante.Domain.Exceptions;

public class ComprobanteNotFoundException extends RuntimeException {
    public ComprobanteNotFoundException(Long id) {
        super("No se encontró el comprobante con ID: " + id);
    }

    public ComprobanteNotFoundException(String mensaje) {
        super(mensaje);
    }
}
