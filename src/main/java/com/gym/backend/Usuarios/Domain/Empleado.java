package com.gym.backend.Usuarios.Domain;

import com.gym.backend.Usuarios.Domain.Enum.Turno;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad de dominio para datos específicos de EMPLEADOS
 * (Administradores, Recepcionistas, Entrenadores, Contadores)
 * Relación 1:1 con Persona
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {
    private Long id;
    private Long personaId;
    private Long usuarioId; // Referencia directa para facilitar consultas

    // Datos laborales
    private String codigoEmpleado;
    private LocalDate fechaContratacion;
    private BigDecimal salario;
    private Turno turno;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private String tipoContrato; // TIEMPO_COMPLETO, MEDIO_TIEMPO, FREELANCE

    // Estado
    private Boolean activo;
    private LocalDate fechaBaja;

    // Metadatos
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Para facilitar acceso
    private Persona persona;

    public String getNombreCompleto() {
        return persona != null ? persona.getNombreCompleto() : null;
    }

    public boolean estaActivo() {
        return Boolean.TRUE.equals(activo) && fechaBaja == null;
    }
}
