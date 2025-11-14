package com.gym.backend.Auth.Domain;

public record RegisterCommand(String nombre,
                              String apellido,
                              String email,
                              String dni,
                              String telefono,
                              String direccion,
                              String password,
                              String rol) {
}
