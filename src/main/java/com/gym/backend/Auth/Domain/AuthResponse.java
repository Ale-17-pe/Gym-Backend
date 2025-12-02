package com.gym.backend.Auth.Domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AuthResponse(
        String token,
        String tipoToken,
        Long usuarioId,
        String nombreCompleto,
        String email,
        String dni,
        String rol,
        String genero,
        Boolean activo,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime expiracion,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime fechaLogin,

        // Campos para 2FA
        Boolean requires2FA,
        String message) {
    public AuthResponse {
        if (fechaLogin == null)
            fechaLogin = LocalDateTime.now();
    }
}