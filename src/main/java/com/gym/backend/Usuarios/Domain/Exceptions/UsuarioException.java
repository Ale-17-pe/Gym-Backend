package com.gym.backend.Usuarios.Domain.Exceptions;

public abstract class UsuarioException extends RuntimeException {
    private final String code;

    public UsuarioException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
