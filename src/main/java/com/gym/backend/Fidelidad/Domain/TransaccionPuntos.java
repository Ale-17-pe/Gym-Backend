package com.gym.backend.Fidelidad.Domain;

import com.gym.backend.Fidelidad.Domain.Enum.MotivoGanancia;
import com.gym.backend.Fidelidad.Domain.Enum.TipoTransaccion;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa una transacción de puntos (historial)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionPuntos {
    private Long id;
    private Long usuarioId;
    private TipoTransaccion tipo;
    private MotivoGanancia motivo;
    private Integer puntos; // Positivo = ganancia, Negativo = gasto
    private String descripcion;
    private Long referenciaId; // ID del pago, asistencia, canje, etc.
    private String tipoReferencia; // "PAGO", "ASISTENCIA", "CANJE"
    private LocalDateTime fecha;

    /**
     * Crea una transacción de ganancia de puntos
     */
    public static TransaccionPuntos crearGanancia(Long usuarioId, MotivoGanancia motivo,
            int puntos, String descripcion,
            Long referenciaId, String tipoReferencia) {
        return TransaccionPuntos.builder()
                .usuarioId(usuarioId)
                .tipo(TipoTransaccion.GANANCIA)
                .motivo(motivo)
                .puntos(Math.abs(puntos))
                .descripcion(descripcion)
                .referenciaId(referenciaId)
                .tipoReferencia(tipoReferencia)
                .fecha(LocalDateTime.now())
                .build();
    }

    /**
     * Crea una transacción de canje de puntos
     */
    public static TransaccionPuntos crearCanje(Long usuarioId, int puntos,
            String descripcion, Long canjeId) {
        return TransaccionPuntos.builder()
                .usuarioId(usuarioId)
                .tipo(TipoTransaccion.CANJE)
                .motivo(null)
                .puntos(-Math.abs(puntos))
                .descripcion(descripcion)
                .referenciaId(canjeId)
                .tipoReferencia("CANJE")
                .fecha(LocalDateTime.now())
                .build();
    }

    /**
     * Crea una transacción de ajuste administrativo
     */
    public static TransaccionPuntos crearAjuste(Long usuarioId, int puntos, String descripcion) {
        return TransaccionPuntos.builder()
                .usuarioId(usuarioId)
                .tipo(TipoTransaccion.AJUSTE)
                .motivo(MotivoGanancia.AJUSTE_ADMIN)
                .puntos(puntos)
                .descripcion(descripcion)
                .referenciaId(null)
                .tipoReferencia("AJUSTE")
                .fecha(LocalDateTime.now())
                .build();
    }

    /**
     * Verifica si es una ganancia
     */
    public boolean esGanancia() {
        return tipo == TipoTransaccion.GANANCIA || puntos > 0;
    }
}
