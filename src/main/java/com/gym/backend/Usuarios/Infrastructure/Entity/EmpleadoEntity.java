package com.gym.backend.Usuarios.Infrastructure.Entity;

import com.gym.backend.Usuarios.Domain.Enum.Turno;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad JPA para datos específicos de EMPLEADOS
 * Relación 1:1 con Persona
 */
@Entity
@Table(name = "empleado")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", unique = true, nullable = false)
    private PersonaEntity persona;

    // Campo directo para consultas rápidas
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "codigo_empleado", unique = true, length = 20)
    private String codigoEmpleado;

    @Column(name = "fecha_contratacion")
    private LocalDate fechaContratacion;

    @Column(precision = 10, scale = 2)
    private BigDecimal salario;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Turno turno;

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @Column(name = "tipo_contrato", length = 30)
    private String tipoContrato;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Relación con Entrenador (si es entrenador)
    @OneToOne(mappedBy = "empleado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EntrenadorEntity entrenador;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (fechaContratacion == null)
            fechaContratacion = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
