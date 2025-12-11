package com.gym.backend.Usuarios.Domain;

import com.gym.backend.Usuarios.Domain.Enum.Genero;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad de dominio para datos personales
 * Compartida por todos los roles (clientes, empleados, etc.)
 * Relación 1:1 con Usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Persona {
    private Long id;
    private Long usuarioId;

    // Datos personales básicos
    private String nombre;
    private String apellido;
    private String dni;
    private Genero genero;
    private LocalDate fechaNacimiento;

    // Contacto
    private String telefono;
    private String direccion;
    private String fotoPerfilUrl;

    // Metadatos
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public Integer getEdad() {
        if (fechaNacimiento == null)
            return null;
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }
}
