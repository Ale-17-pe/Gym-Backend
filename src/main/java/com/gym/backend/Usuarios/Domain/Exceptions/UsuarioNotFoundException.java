package com.gym.backend.Usuarios.Domain.Exceptions;

public class UsuarioNotFoundException extends UsuarioException {
    public UsuarioNotFoundException(Long id) {
        super("Usuario con ID " + id + " no encontrado", "USUARIO_NOT_FOUND");
    }

    public UsuarioNotFoundException(String email) {
        super("Usuario con email " + email + " no encontrado", "USUARIO_NOT_FOUND");
    }
}
