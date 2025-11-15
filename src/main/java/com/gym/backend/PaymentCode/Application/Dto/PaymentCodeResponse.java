package com.gym.backend.PaymentCode.Application.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentCodeResponse {
    private Long id;
    private Long pagoId;
    private String codigo;
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaExpiracion;
    private String estado;
    private boolean expirado;
    private boolean puedeUsar;
    private String tiempoRestante;
}