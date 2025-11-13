package com.gym.backend.HistorialMembresias.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "historial_membresias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialMembresiaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;
    private Long planId;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private String accion;
    private String estado;
}