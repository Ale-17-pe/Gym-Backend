package com.gym.backend.Clases.Infrastructure.Entity;

import com.gym.backend.Clases.Domain.Enum.EstadoReserva;
import com.gym.backend.Usuarios.Infrastructure.Entity.UsuarioEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservas_clase", uniqueConstraints = @UniqueConstraint(columnNames = { "sesion_clase_id",
        "usuario_id" }))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReservaClaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sesion_clase_id", nullable = false)
    private SesionClaseEntity sesionClase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;

    private Boolean asistio;

    @Column(name = "posicion_lista_espera")
    private Integer posicionListaEspera;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (fechaReserva == null) {
            fechaReserva = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoReserva.CONFIRMADA;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
