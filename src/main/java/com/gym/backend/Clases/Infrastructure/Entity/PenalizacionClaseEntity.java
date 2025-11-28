package com.gym.backend.Clases.Infrastructure.Entity;

import com.gym.backend.Clases.Domain.Enum.TipoPenalizacion;
import com.gym.backend.Usuarios.Infrastructure.Entity.UsuarioEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "penalizaciones_clase")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PenalizacionClaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_clase_id", nullable = false)
    private ReservaClaseEntity reservaClase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoPenalizacion tipo;

    @Column(nullable = false)
    private Integer puntos;

    @Column(name = "fecha_penalizacion", nullable = false)
    private LocalDateTime fechaPenalizacion;

    @Column(nullable = false)
    private Boolean activo;

    @PrePersist
    protected void onCreate() {
        if (fechaPenalizacion == null) {
            fechaPenalizacion = LocalDateTime.now();
        }
        if (puntos == null) {
            puntos = 1;
        }
        if (activo == null) {
            activo = true;
        }
    }
}
