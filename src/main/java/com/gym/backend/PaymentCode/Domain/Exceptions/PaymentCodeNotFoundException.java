package com.gym.backend.PaymentCode.Domain.Exceptions;

public class PaymentCodeNotFoundException extends PaymentCodeException {
    public PaymentCodeNotFoundException(String codigo) {
        super("Código de pago no encontrado: " + codigo, "PAYMENT_CODE_NOT_FOUND");
    }

    public PaymentCodeNotFoundException(Long id) {
        super("Código de pago con ID " + id + " no encontrado", "PAYMENT_CODE_NOT_FOUND");
    }
}