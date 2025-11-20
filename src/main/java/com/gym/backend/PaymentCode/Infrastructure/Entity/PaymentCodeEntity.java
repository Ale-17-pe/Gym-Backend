package com.gym.backend.PaymentCode.Infrastructure.Entity;

import com.gym.backend.PaymentCode.Domain.Enums.EstadoPaymentCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_codes")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pagoId;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPaymentCode estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (fechaGeneracion == null) {
            fechaGeneracion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoPaymentCode.GENERADO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}