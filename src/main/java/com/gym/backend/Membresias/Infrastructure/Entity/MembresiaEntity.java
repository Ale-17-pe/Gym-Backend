package com.gym.backend.Membresias.Infrastructure.Entity;

import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "membresias")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MembresiaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;
    @Column(nullable = false)
    private Long planId;
    @Column(nullable = false)
    private Long pagoId;
    @Column(nullable = false)
    private LocalDate fechaInicio;
    @Column(nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMembresia estado;

    @Column(nullable = false)
    private LocalDate fechaCreacion;
    @Column(nullable = false)
    private LocalDate fechaActualizacion;

    @Column(length = 50)
    private String codigoAcceso;

    @Column
    private LocalDateTime codigoExpiracion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDate.now();
        fechaActualizacion = LocalDate.now();
        if (estado == null)
            throw new IllegalStateException("Estado no puede ser null");
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDate.now();
    }
}