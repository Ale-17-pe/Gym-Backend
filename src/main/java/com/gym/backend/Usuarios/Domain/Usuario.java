package com.gym.backend.Usuarios.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Usuario {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String dni;
    private String telefono;
    private String direccion;
    private String password;
    private String rol;

    private Boolean activo;
}