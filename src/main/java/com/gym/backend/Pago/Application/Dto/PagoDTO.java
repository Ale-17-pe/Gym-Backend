package com.gym.backend.Pago.Application.Dto;

import com.gym.backend.Pago.Domain.Enum.EstadoPago;
import com.gym.backend.Pago.Domain.Enum.MetodoPago;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PagoDTO {
    private Long id;
    private Long usuarioId;
    private Long planId;
    private Double monto;
    private EstadoPago estado;
    private MetodoPago metodoPago;
    private String referencia;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaVencimiento;
    private String codigoPago;
}