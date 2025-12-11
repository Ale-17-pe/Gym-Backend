package com.gym.backend.Auth.Domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request para reenviar código de verificación de email
 */
public record ResendVerificationRequest(
        @NotBlank(message = "Email es requerido") @Email(message = "Email inválido") String email) {
}
