package com.gym.backend.Usuarios.Application.Dto;

import com.gym.backend.Usuarios.Domain.Enum.Genero;
import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.Pattern;

@Data
public class ActualizarUsuarioRequest {
    private String nombre;
    private String apellido;
    @Pattern(regexp = "^\\d{8}[A-Za-z]?$", message = "El DNI debe tener 8 dígitos")
    private String dni;
    private Genero genero;
    private LocalDate fechaNacimiento;
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "El teléfono debe ser válido")
    private String telefono;
    private String direccion;
    private String rol;
    private Boolean activo;
}