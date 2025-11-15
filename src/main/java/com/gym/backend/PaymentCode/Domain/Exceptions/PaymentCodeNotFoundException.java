package com.gym.backend.PaymentCode.Domain.Exceptions;

public class PaymentCodeNotFoundException extends PaymentCodeException {
    public PaymentCodeNotFoundException(String codigo) {
        super("Código de pago no encontrado: " + codigo, "PAYMENT_CODE_NOT_FOUND");
    }
}