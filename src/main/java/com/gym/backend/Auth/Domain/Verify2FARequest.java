package com.gym.backend.Auth.Domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Verify2FARequest(
        @NotBlank(message = "Email es requerido") @Email(message = "Email inválido") String email,

        @NotBlank(message = "Código es requerido") @Size(min = 6, max = 6, message = "El código debe tener 6 dígitos") String code) {
}
