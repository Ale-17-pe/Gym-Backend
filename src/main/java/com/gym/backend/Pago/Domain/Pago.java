package com.gym.backend.Pago.Domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class Pago {

    private Long id;
    private Long usuarioId;
    private Long membresiaId;

    private Double monto;
    private String estado; // PENDIENTE, CONFIRMADO, RECHAZADO

    private LocalDateTime fechaPago;
}
