package com.gym.backend.Usuarios.Application.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Respuesta con datos de usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String genero;
    private String dni;
    private String telefono;
    private String direccion;
    private String rol;
    private Boolean activo;
    private Boolean emailVerificado;
    private LocalDate fechaNacimiento;
}