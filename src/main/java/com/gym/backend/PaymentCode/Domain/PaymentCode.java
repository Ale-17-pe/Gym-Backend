package com.gym.backend.PaymentCode.Domain;

import com.gym.backend.PaymentCode.Domain.Enums.EstadoPaymentCode;
import com.gym.backend.PaymentCode.Domain.Exceptions.PaymentCodeValidationException;
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
    private String codigo;
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaExpiracion;
    private EstadoPaymentCode estado;

    public void validar() {
        if (pagoId == null) throw new PaymentCodeValidationException("El ID de pago es requerido");
        if (codigo == null || codigo.trim().isEmpty()) throw new PaymentCodeValidationException("El código es requerido");
        if (fechaExpiracion == null) throw new PaymentCodeValidationException("La fecha de expiración es requerida");
    }

    public boolean estaExpirado() { return fechaExpiracion.isBefore(LocalDateTime.now()); }
    public boolean puedeSerUsado() { return estado == EstadoPaymentCode.GENERADO && !estaExpirado(); }
    public void marcarComoUsado() { this.estado = EstadoPaymentCode.USADO; }
    public void marcarComoExpirado() { this.estado = EstadoPaymentCode.EXPIRADO; }
    public boolean esValido() { return estado == EstadoPaymentCode.GENERADO && !estaExpirado(); }
}
