package com.gym.backend.Comprobante.Domain.Exceptions;

public class ComprobanteDuplicadoException extends RuntimeException {
    public ComprobanteDuplicadoException(Long pagoId) {
        super("Ya existe un comprobante generado para el pago con ID: " + pagoId);
    }
}
