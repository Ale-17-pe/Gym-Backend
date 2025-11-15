package com.gym.backend.PaymentCode.Application.Dto;

import com.gym.backend.PaymentCode.Domain.Enums.EstadoPaymentCode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentCodeDTO {
    private Long id;
    private Long pagoId;
    private String codigo;
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaExpiracion;
    private EstadoPaymentCode estado;
}