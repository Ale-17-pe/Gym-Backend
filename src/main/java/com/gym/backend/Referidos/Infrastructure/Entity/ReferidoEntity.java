package com.gym.backend.Referidos.Infrastructure.Entity;

import com.gym.backend.Referidos.Domain.Referido.EstadoReferido;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "referidos", indexes = {
        @Index(name = "idx_referidos_referidor", columnList = "referidor_id"),
        @Index(name = "idx_referidos_referido", columnList = "referido_id"),
        @Index(name = "idx_referidos_codigo", columnList = "codigo_referido")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "referidor_id", nullable = false)
    private Long referidorId;

    @Column(name = "referido_id", nullable = false, unique = true)
    private Long referidoId;

    @Column(name = "codigo_referido", nullable = false, length = 20)
    private String codigoReferido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoReferido estado = EstadoReferido.PENDIENTE;

    @Column(name = "fecha_referido", nullable = false)
    private LocalDateTime fechaReferido;

    @Column(name = "fecha_completado")
    private LocalDateTime fechaCompletado;

    @Column(name = "puntos_otorgados")
    private Integer puntosOtorgados;

    @PrePersist
    protected void onCreate() {
        if (fechaReferido == null) {
            fechaReferido = LocalDateTime.now();
        }
    }
}
