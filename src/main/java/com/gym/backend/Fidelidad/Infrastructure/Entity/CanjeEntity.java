package com.gym.backend.Fidelidad.Infrastructure.Entity;

import com.gym.backend.Fidelidad.Domain.Enum.EstadoCanje;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA para los canjes de puntos
 */
@Entity
@Table(name = "canjes", indexes = {
        @Index(name = "idx_canjes_usuario", columnList = "usuario_id"),
        @Index(name = "idx_canjes_codigo", columnList = "codigo_canje"),
        @Index(name = "idx_canjes_estado", columnList = "estado")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CanjeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "recompensa_id", nullable = false)
    private Long recompensaId;

    @Column(name = "puntos_usados", nullable = false)
    private Integer puntosUsados;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoCanje estado = EstadoCanje.PENDIENTE;

    @Column(name = "codigo_canje", nullable = false, unique = true, length = 20)
    private String codigoCanje;

    @Column(name = "fecha_canje", nullable = false)
    private LocalDateTime fechaCanje;

    @Column(name = "fecha_uso")
    private LocalDateTime fechaUso;

    @Column(name = "usado_en_pago_id")
    private Long usadoEnPagoId;

    // Datos desnormalizados para historial
    @Column(name = "nombre_recompensa", length = 100)
    private String nombreRecompensa;

    @Column(name = "descripcion_recompensa", length = 255)
    private String descripcionRecompensa;

    @PrePersist
    protected void onCreate() {
        if (fechaCanje == null) {
            fechaCanje = LocalDateTime.now();
        }
    }
}
