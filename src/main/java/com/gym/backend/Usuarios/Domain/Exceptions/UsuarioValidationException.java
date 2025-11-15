package com.gym.backend.Usuarios.Domain.Exceptions;

public class UsuarioValidationException extends UsuarioException {
  public UsuarioValidationException(String message) {
    super(message, "USUARIO_VALIDACION");
  }
}
