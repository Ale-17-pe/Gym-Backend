package com.gym.backend.Fidelidad.Infrastructure.Entity;

import com.gym.backend.Fidelidad.Domain.Enum.MotivoGanancia;
import com.gym.backend.Fidelidad.Domain.Enum.TipoTransaccion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA para el historial de transacciones de puntos
 */
@Entity
@Table(name = "transacciones_puntos", indexes = {
        @Index(name = "idx_transacciones_usuario", columnList = "usuario_id"),
        @Index(name = "idx_transacciones_fecha", columnList = "fecha"),
        @Index(name = "idx_transacciones_referencia", columnList = "referencia_id, tipo_referencia")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionPuntosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoTransaccion tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo", length = 30)
    private MotivoGanancia motivo;

    @Column(name = "puntos", nullable = false)
    private Integer puntos;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "tipo_referencia", length = 30)
    private String tipoReferencia;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
