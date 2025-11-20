package com.gym.backend.PaymentCode.Domain;

import com.gym.backend.PaymentCode.Domain.Enums.EstadoPaymentCode;
import com.gym.backend.PaymentCode.Domain.Exceptions.PaymentCodeValidationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PaymentCode {
    private Long id;
    private Long pagoId;
    private String codigo;
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaExpiracion;
    private EstadoPaymentCode estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public void validar() {
        if (pagoId == null) throw new PaymentCodeValidationException("El ID de pago es requerido");
        if (codigo == null || codigo.trim().isEmpty()) throw new PaymentCodeValidationException("El código es requerido");
        if (fechaExpiracion == null) throw new PaymentCodeValidationException("La fecha de expiración es requerida");
        if (fechaExpiracion.isBefore(LocalDateTime.now())) {
            throw new PaymentCodeValidationException("La fecha de expiración no puede ser en el pasado");
        }
    }

    public boolean estaExpirado() {
        return fechaExpiracion.isBefore(LocalDateTime.now());
    }

    public boolean estaPorVencer() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime unaHoraAntes = fechaExpiracion.minusHours(1);
        return !estaExpirado() && (ahora.isAfter(unaHoraAntes) || ahora.isEqual(unaHoraAntes));
    }

    public boolean puedeSerUsado() {
        return estado == EstadoPaymentCode.GENERADO && !estaExpirado();
    }

    public void marcarComoUsado() {
        this.estado = EstadoPaymentCode.USADO;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void marcarComoExpirado() {
        this.estado = EstadoPaymentCode.EXPIRADO;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void cancelar() {
        this.estado = EstadoPaymentCode.CANCELADO;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean esValido() {
        return estado == EstadoPaymentCode.GENERADO && !estaExpirado();
    }

    public long minutosRestantes() {
        if (estaExpirado()) return 0;
        java.time.Duration duracion = java.time.Duration.between(LocalDateTime.now(), fechaExpiracion);
        return duracion.toMinutes();
    }

    public long horasRestantes() {
        if (estaExpirado()) return 0;
        java.time.Duration duracion = java.time.Duration.between(LocalDateTime.now(), fechaExpiracion);
        return duracion.toHours();
    }
}