package com.gym.backend.PaymentCode.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pagoId;

    private String codigo;

    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaExpiracion;

    private String estado;
}
