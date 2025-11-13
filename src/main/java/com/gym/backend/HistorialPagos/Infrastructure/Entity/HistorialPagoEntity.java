package com.gym.backend.HistorialPagos.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialPagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pagoId;
    private Long usuarioId;
    private Double monto;

    private String estadoAnterior;
    private String estadoNuevo;

    private LocalDateTime fechaCambio;
}
