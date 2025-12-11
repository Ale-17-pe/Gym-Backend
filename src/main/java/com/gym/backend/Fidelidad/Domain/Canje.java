package com.gym.backend.Fidelidad.Domain;

import com.gym.backend.Fidelidad.Domain.Enum.EstadoCanje;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio que representa un canje de puntos
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Canje {
    private Long id;
    private Long usuarioId;
    private Long recompensaId;
    private Integer puntosUsados;
    private EstadoCanje estado;
    private String codigoCanje; // Código único para presentar
    private LocalDateTime fechaCanje;
    private LocalDateTime fechaUso; // Cuando se utilizó
    private Long usadoEnPagoId; // Si es descuento, el pago donde se aplicó

    // Datos de la recompensa (para mostrar en historial)
    private String nombreRecompensa;
    private String descripcionRecompensa;

    /**
     * Crea un nuevo canje
     */
    public static Canje crear(Long usuarioId, Recompensa recompensa) {
        return Canje.builder()
                .usuarioId(usuarioId)
                .recompensaId(recompensa.getId())
                .puntosUsados(recompensa.getCostoPuntos())
                .estado(EstadoCanje.PENDIENTE)
                .codigoCanje(generarCodigoCanje())
                .fechaCanje(LocalDateTime.now())
                .nombreRecompensa(recompensa.getNombre())
                .descripcionRecompensa(recompensa.getDescripcion())
                .build();
    }

    /**
     * Genera un código único de canje
     */
    private static String generarCodigoCanje() {
        return "CJ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Marca el canje como completado
     */
    public void completar() {
        this.estado = EstadoCanje.COMPLETADO;
        this.fechaUso = LocalDateTime.now();
    }

    /**
     * Marca el canje como completado con referencia al pago
     */
    public void completarEnPago(Long pagoId) {
        this.estado = EstadoCanje.COMPLETADO;
        this.fechaUso = LocalDateTime.now();
        this.usadoEnPagoId = pagoId;
    }

    /**
     * Cancela el canje
     */
    public void cancelar() {
        if (this.estado == EstadoCanje.COMPLETADO) {
            throw new IllegalStateException("No se puede cancelar un canje ya completado");
        }
        this.estado = EstadoCanje.CANCELADO;
    }

    /**
     * Verifica si el canje está pendiente
     */
    public boolean estaPendiente() {
        return estado == EstadoCanje.PENDIENTE;
    }

    /**
     * Verifica si el canje puede ser utilizado
     */
    public boolean puedeUsarse() {
        return estado == EstadoCanje.PENDIENTE;
    }
}
