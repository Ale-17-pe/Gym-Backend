package com.gym.backend.Auth.Domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "El email es requerido") String email,

        @NotBlank(message = "El código es requerido") @Pattern(regexp = "^\\d{6}$", message = "El código debe ser de 6 dígitos") String code,

        @NotBlank(message = "La contraseña es requerida") @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String newPassword) {
}
