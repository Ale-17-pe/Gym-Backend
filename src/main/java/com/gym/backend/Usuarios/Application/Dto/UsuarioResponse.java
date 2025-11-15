package com.gym.backend.Usuarios.Application.Dto;

import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import lombok.Data;

@Data
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private Genero genero;
    private String email;
    private String dni;
    private String telefono;
    private String direccion;
    private String rol;
    private Boolean activo;
    private String password;
}