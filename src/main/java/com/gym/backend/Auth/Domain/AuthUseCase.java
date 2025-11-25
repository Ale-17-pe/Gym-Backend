package com.gym.backend.Auth.Domain;

import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class    AuthUseCase {

    private final AuthServicePort authPort;

    public AuthResponse login(LoginCommand command) {
        validarLogin(command);
        return authPort.login(command);
    }

    public AuthResponse registrar(RegisterCommand command) {
        validarRegistro(command);
        return authPort.registrar(command);
    }

    public void validarToken(String token) {
        authPort.validarToken(token);
    }

    private void validarLogin(LoginCommand command) {
        if (command.emailOrDni() == null || command.emailOrDni().trim().isEmpty()) {
            throw new UsuarioValidationException("Email o DNI es requerido");
        }
        if (command.password() == null || command.password().trim().isEmpty()) {
            throw new UsuarioValidationException("Contraseña es requerida");
        }
    }

    private void validarRegistro(RegisterCommand command) {
        if (command.nombre() == null || command.nombre().trim().isEmpty()) {
            throw new UsuarioValidationException("Nombre es requerido");
        }
        if (command.dni() == null || command.dni().trim().isEmpty()) {
            throw new UsuarioValidationException("DNI es requerido");
        }
        if (command.email() == null || command.email().trim().isEmpty()) {
            throw new UsuarioValidationException("Email es requerido");
        }
    }
}