package com.gym.backend.Comprobante.Domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comprobante {
    private Long id;
    private String numeroComprobante;
    private Long pagoId;
    private Long usuarioId;
    private LocalDateTime fechaEmision;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private BigDecimal descuento;
    private byte[] pdfData;
    private String emitidoPor;
    private LocalDateTime fechaCreacion;
}
