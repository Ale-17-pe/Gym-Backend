package com.gym.backend.HistorialPagos.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class HistorialPago {

    private Long id;
    private Long pagoId;
    private Long usuarioId;
    private Double monto;

    private String estadoAnterior;
    private String estadoNuevo;

    private LocalDateTime fechaCambio;
}