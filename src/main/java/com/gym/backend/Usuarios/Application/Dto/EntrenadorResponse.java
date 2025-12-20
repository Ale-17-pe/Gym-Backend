package com.gym.backend.Usuarios.Application.Dto;

import com.gym.backend.Usuarios.Domain.Enum.EspecialidadEntrenador;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de Entrenador - evita problemas de serialización circular
 */
@Data
@Builder
public class EntrenadorResponse {
    private Long id;
    private Long usuarioId;

    // Datos de la persona
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String fotoUrl;

    // Datos del entrenador
    private EspecialidadEntrenador especialidad;
    private String especialidadNombre;
    private String certificaciones;
    private Integer experienciaAnios;
    private Integer maxClientes;
    private String biografia;
    private Double ratingPromedio;
    private Integer totalResenas;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
