package com.gym.backend.Usuarios.Application.Dto;

import com.gym.backend.Usuarios.Domain.Enum.Genero;
import lombok.Data;

@Data
public class ActualizarUsuarioRequest {
    private String nombre;
    private String apellido;
    private Genero genero;
    private String telefono;
    private String direccion;
    private String rol;
    private Boolean activo;
}