package com.gym.backend.Comprobante.Application.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobanteResponse {
    private Long id;
    private String numeroComprobante;
    private Long pagoId;
    private Long usuarioId;
    private LocalDateTime fechaEmision;
    private Double subtotal;
    private Double igv;
    private Double total;
    private Double descuento;
    private String emitidoPor;
    private LocalDateTime fechaCreacion;

    // NO incluir el pdfData en la respuesta por defecto (es muy pesado)
    // Se descargará por un endpoint separado
}
