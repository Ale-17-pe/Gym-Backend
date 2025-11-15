package com.gym.backend.Auth.Domain;

import jakarta.validation.constraints.NotBlank;

public record LoginCommand(
        @NotBlank String emailOrDni,
        @NotBlank String password
) {}
