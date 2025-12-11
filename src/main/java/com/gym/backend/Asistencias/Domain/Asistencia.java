package com.gym.backend.Asistencias.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Modelo de dominio para Asistencia - NORMALIZADO (3NF)
 * El usuario_id se obtiene de la membresía relacionada.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Asistencia {
    // Atributos
    private Long id;
    // ELIMINADO por 3NF: usuarioId (se obtiene de membresias)
    private Long membresiaId;
    private LocalDateTime fechaHora;
    private String tipo; // ENTRADA, SALIDA
    private String estado; // REGISTRADA, CANCELADA
    private String observaciones;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Métodos de negocio
    public boolean esDelDia(LocalDateTime fecha) {
        LocalDateTime inicioDia = fecha.toLocalDate().atStartOfDay();
        LocalDateTime finDia = fecha.toLocalDate().atTime(23, 59, 59);
        return !fechaHora.isBefore(inicioDia) && !fechaHora.isAfter(finDia);
    }

    public boolean esEntrada() {
        return "ENTRADA".equals(tipo);
    }

    public boolean esSalida() {
        return "SALIDA".equals(tipo);
    }

    public void cancelar() {
        this.estado = "CANCELADA";
        this.fechaActualizacion = LocalDateTime.now();
    }
}