package com.gym.backend.Pago.Domain;

import com.gym.backend.Pago.Domain.Enum.EstadoPago;
import com.gym.backend.Pago.Domain.Enum.MetodoPago;
import com.gym.backend.Pago.Domain.Exceptions.PagoValidationException;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Pago {
    private Long id;
    private Long usuarioId;
    private Long planId;
    private Double monto;
    private EstadoPago estado;
    private MetodoPago metodoPago;
    private String referencia;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaActualizacion;

    public void validar() {
        if (usuarioId == null) throw new PagoValidationException("El ID de usuario es requerido");
        if (planId == null) throw new PagoValidationException("El ID de plan es requerido");
        if (monto == null || monto <= 0) throw new PagoValidationException("El monto debe ser mayor a 0");
        if (metodoPago == null) throw new PagoValidationException("El método de pago es requerido");
    }

    public boolean esConfirmado() { return EstadoPago.CONFIRMADO.equals(estado); }
    public boolean esPendiente() { return EstadoPago.PENDIENTE.equals(estado); }
    public void confirmar() {
        this.estado = EstadoPago.CONFIRMADO;
        this.fechaPago = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }
    public void rechazar() {
        this.estado = EstadoPago.RECHAZADO;
        this.fechaActualizacion = LocalDateTime.now();
    }
    public void cancelar() {
        this.estado = EstadoPago.CANCELADO;
        this.fechaActualizacion = LocalDateTime.now();
    }
    public boolean puedeSerConfirmado() { return esPendiente(); }
}