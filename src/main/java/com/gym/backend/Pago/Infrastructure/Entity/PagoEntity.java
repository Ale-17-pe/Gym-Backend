package com.gym.backend.Pago.Infrastructure.Entity;

import com.gym.backend.Pago.Domain.Enum.EstadoPago;
import com.gym.backend.Pago.Domain.Enum.MetodoPago;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PagoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private Long usuarioId;
    @Column(nullable = false) private Long planId;
    @Column(nullable = false) private Double monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20) private EstadoPago estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30) private MetodoPago metodoPago;

    @Column(length = 100) private String referencia;
    @Column(nullable = false) private LocalDateTime fechaCreacion;
    private LocalDateTime fechaPago;
    @Column(nullable = false) private LocalDateTime fechaActualizacion;

    @PrePersist protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    @PreUpdate protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
