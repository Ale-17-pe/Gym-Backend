package com.gym.backend.Auth.Domain;

import jakarta.validation.constraints.NotBlank;

public record RegisterCommand(
                @NotBlank String nombre,
                @NotBlank String apellido,
                String genero,
                @NotBlank String email,
                @NotBlank String dni,
                String telefono,
                String direccion,
                @NotBlank String password,
                String rol) {
}
