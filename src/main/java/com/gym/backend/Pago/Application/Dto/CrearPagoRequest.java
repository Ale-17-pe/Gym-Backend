package com.gym.backend.Pago.Application.Dto;

import com.gym.backend.Pago.Domain.Enum.MetodoPago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CrearPagoRequest {
    @NotNull private Long usuarioId;
    @NotNull private Long planId;
    @NotNull @Positive private Double monto;
    @NotNull private MetodoPago metodoPago;
    private String referencia;
}