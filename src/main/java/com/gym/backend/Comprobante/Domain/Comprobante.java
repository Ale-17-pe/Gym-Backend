package com.gym.backend.Comprobante.Domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo de dominio para Comprobante - NORMALIZADO (3NF)
 * El usuario_id se obtiene del pago relacionado.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comprobante {
    private Long id;
    private String numeroComprobante;
    private Long pagoId;
    // ELIMINADO por 3NF: usuarioId (se obtiene de pagos)
    private LocalDateTime fechaEmision;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private BigDecimal descuento;
    private byte[] pdfData;
    private String emitidoPor;
    private LocalDateTime fechaCreacion;
}
