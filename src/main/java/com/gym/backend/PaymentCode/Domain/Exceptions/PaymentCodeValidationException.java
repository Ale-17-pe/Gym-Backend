package com.gym.backend.PaymentCode.Domain.Exceptions;

public class PaymentCodeValidationException extends PaymentCodeException {
    public PaymentCodeValidationException(String message) {
        super(message, "PAYMENT_CODE_VALIDACION");
    }
}