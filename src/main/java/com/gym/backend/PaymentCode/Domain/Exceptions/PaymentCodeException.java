package com.gym.backend.PaymentCode.Domain.Exceptions;

public abstract class PaymentCodeException extends RuntimeException {
    private final String code;
    public PaymentCodeException(String message, String code) {
        super(message); this.code = code;
    }
    public String getCode() { return code; }
}