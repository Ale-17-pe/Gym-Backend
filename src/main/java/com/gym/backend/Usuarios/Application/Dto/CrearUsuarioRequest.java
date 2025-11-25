package com.gym.backend.Usuarios.Application.Dto;

import com.gym.backend.Usuarios.Domain.Enum.Genero;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class CrearUsuarioRequest {
    private String nombre;
    private String apellido;
    private Genero genero;
    private String email;
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^\\d{8}[A-Za-z]?$", message = "El DNI debe tener 8 dígitos")
    private String dni;
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "El teléfono debe ser válido")
    private String telefono;
    private String direccion;
    private String password;
    private String rol;
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;
}