package com.gym.backend.Usuarios.Infrastructure.Entity;

import com.gym.backend.Usuarios.Domain.Enum.NivelExperiencia;
import com.gym.backend.Usuarios.Domain.Enum.ObjetivoFitness;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA para datos específicos de CLIENTES
 * Relación 1:1 con Persona
 */
@Entity
@Table(name = "cliente")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", unique = true, nullable = false)
    private PersonaEntity persona;

    // Campo directo para consultas rápidas
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "objetivo_fitness")
    private ObjetivoFitness objetivoFitness;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_experiencia")
    private NivelExperiencia nivelExperiencia;

    @Column(name = "condiciones_medicas", columnDefinition = "TEXT")
    private String condicionesMedicas;

    @Column(name = "contacto_emergencia_nombre", length = 100)
    private String contactoEmergenciaNombre;

    @Column(name = "contacto_emergencia_telefono", length = 20)
    private String contactoEmergenciaTelefono;

    @Column(name = "como_nos_conocio", length = 50)
    private String comoNosConocio;

    @Column(name = "codigo_referido", length = 50)
    private String codigoReferido;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "fecha_registro_gym")
    private LocalDate fechaRegistroGym;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (fechaRegistroGym == null)
            fechaRegistroGym = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
