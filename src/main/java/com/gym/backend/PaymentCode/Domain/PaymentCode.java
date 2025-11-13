package com.gym.backend.PaymentCode.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PaymentCode {

    private Long id;
    private Long pagoId;

    private String codigo; // Ejemplo: GYM-912AF3
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaExpiracion;

    private String estado; // GENERADO, USADO, EXPIRADO
}