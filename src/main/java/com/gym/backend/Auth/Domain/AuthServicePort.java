package com.gym.backend.Auth.Domain;

public interface AuthServicePort {
    AuthResponse login(LoginCommand command);
    AuthResponse registrar(RegisterCommand command);
    void validarToken(String token);
}
