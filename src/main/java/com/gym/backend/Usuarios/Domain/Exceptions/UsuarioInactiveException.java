package com.gym.backend.Usuarios.Domain.Exceptions;

public class UsuarioInactiveException extends UsuarioException {
    public UsuarioInactiveException(Long id) {
        super("Usuario con ID " + id + " está inactivo", "USUARIO_INACTIVO");
    }
}
