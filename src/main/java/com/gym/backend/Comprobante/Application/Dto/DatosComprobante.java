package com.gym.backend.Comprobante.Application.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO que contiene todos los datos necesarios para generar un comprobante de
 * pago
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatosComprobante {
    // Información del Gimnasio
    private String nombreGimnasio;
    private String direccionGimnasio;
    private String rucGimnasio;
    private String telefonoGimnasio;

    // Información del Comprobante
    private String numeroComprobante;
    private LocalDateTime fechaEmision;

    // Información del Cliente
    private String nombreCliente;
    private String documentoCliente;
    private Long usuarioId;

    // Información de la Membresía/Plan
    private String conceptoPago; // "Membresía Premium Mensual"
    private String periodoMembresia; // "01/12/2024 - 31/12/2024"
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    // Desglose de Costos
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal igv;
    private BigDecimal total;

    // Información del Pago
    private String metodoPago; // "Efectivo", "Tarjeta", "Yape"
    private String codigoOperacion; // Código de pago o referencia
    private String codigoPago; // Código QRpara el comprobante

    // Información de Auditoría
    private String emitidoPor; // Nombre del recepcionista
    private Long pagoId;
}
