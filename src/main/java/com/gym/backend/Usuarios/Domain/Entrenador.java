package com.gym.backend.Usuarios.Domain;

import com.gym.backend.Usuarios.Domain.Enum.EspecialidadEntrenador;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad de dominio para datos específicos de ENTRENADORES
 * Extensión de Empleado con datos adicionales para entrenadores
 * Relación 1:1 con Empleado
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entrenador {
    private Long id;
    private Long empleadoId;
    private Long usuarioId; // Referencia directa

    // Datos del entrenador
    private EspecialidadEntrenador especialidad;
    private String certificaciones; // JSON o texto separado por comas
    private Integer experienciaAnios;
    private Integer maxClientes; // Máximo de clientes asignados
    private String biografia;

    // Estadísticas
    private Double ratingPromedio;
    private Integer totalResenas;

    // Estado
    private Boolean activo;

    // Metadatos
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Para facilitar acceso
    private Empleado empleado;
    private Persona persona;

    public String getNombreCompleto() {
        if (persona != null)
            return persona.getNombreCompleto();
        if (empleado != null && empleado.getPersona() != null)
            return empleado.getPersona().getNombreCompleto();
        return null;
    }
}
