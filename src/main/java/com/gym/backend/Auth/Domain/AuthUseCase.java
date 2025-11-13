package com.gym.backend.Auth.Domain;

public class AuthUseCase {

    private final AuthServicePort authPort;

    public AuthUseCase(AuthServicePort authPort) {
        this.authPort = authPort;
    }

    public AuthResponse login(LoginCommand command) {
        return authPort.login(command);
    }

    public AuthResponse registrar(RegisterCommand command) {
        return authPort.registrar(command);
    }
}
