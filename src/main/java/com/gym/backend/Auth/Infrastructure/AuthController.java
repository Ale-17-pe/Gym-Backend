package com.gym.backend.Auth.Infrastructure;

import com.gym.backend.Auth.Domain.AuthResponse;
import com.gym.backend.Auth.Domain.AuthUseCase;
import com.gym.backend.Auth.Domain.LoginCommand;
import com.gym.backend.Auth.Domain.RegisterCommand;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase useCase;

    public AuthController(AuthServiceAdapter adapter) {
        this.useCase = new AuthUseCase(adapter);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginCommand command) {
        return useCase.login(command);
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterCommand command) {
        return useCase.registrar(command);
    }
}
