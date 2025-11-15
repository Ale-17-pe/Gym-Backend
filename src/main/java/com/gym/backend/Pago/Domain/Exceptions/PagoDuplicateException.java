package com.gym.backend.Pago.Domain.Exceptions;

public class PagoDuplicateException extends PagoException {
    public PagoDuplicateException(String referencia) {
        super("Ya existe un pago con referencia: " + referencia, "PAGO_DUPLICADO");
    }
}