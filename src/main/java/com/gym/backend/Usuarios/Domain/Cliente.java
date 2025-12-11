package com.gym.backend.Usuarios.Domain;

import com.gym.backend.Usuarios.Domain.Enum.NivelExperiencia;
import com.gym.backend.Usuarios.Domain.Enum.ObjetivoFitness;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad de dominio para datos específicos de CLIENTES del gimnasio
 * Relación 1:1 con Persona (y por transitividad con Usuario)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private Long id;
    private Long personaId;
    private Long usuarioId; // Referencia directa para facilitar consultas

    // Datos del cliente
    private ObjetivoFitness objetivoFitness;
    private NivelExperiencia nivelExperiencia;
    private String condicionesMedicas;

    // Contacto de emergencia
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;

    // Marketing
    private String comoNosConocio; // REDES, REFERIDO, PUBLICIDAD, OTRO
    private String codigoReferido; // Si llegó por referido

    // Estado
    private Boolean activo;
    private LocalDate fechaRegistroGym;

    // Metadatos
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Para facilitar acceso (se carga desde Persona)
    private Persona persona;

    public String getNombreCompleto() {
        return persona != null ? persona.getNombreCompleto() : null;
    }
}
