package com.gym.backend.Pago.Application.Dto;

import lombok.Data;

@Data
public class PagoDTO {

    private Long id;
    private Long usuarioId;
    private Long membresiaId;

    private Double monto;
    private String estado;

    private String fechaPago;
}
