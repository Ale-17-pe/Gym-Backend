package com.gym.backend.Auth.Domain;

public record AuthResponse(String token,
                           Long usuarioId,
                           String rol) {
}
