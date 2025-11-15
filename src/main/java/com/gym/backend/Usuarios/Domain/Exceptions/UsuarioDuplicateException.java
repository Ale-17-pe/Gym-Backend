package com.gym.backend.Usuarios.Domain.Exceptions;

public class UsuarioDuplicateException extends UsuarioException {
    public UsuarioDuplicateException(String campo, String valor) {
        super("Ya existe un usuario con " + campo + ": " + valor, "USUARIO_DUPLICADO");
    }
}