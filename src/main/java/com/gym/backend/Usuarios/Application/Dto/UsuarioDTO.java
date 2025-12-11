package com.gym.backend.Usuarios.Application.Dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * DTO para transferir datos de usuario
 */
@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String genero;
    private String email;
    private String dni;
    private String telefono;
    private String direccion;
    private String rol;
    private Boolean activo;
    private Boolean emailVerificado;
    private LocalDate fechaNacimiento;
}
