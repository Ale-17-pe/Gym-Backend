package com.gym.backend.Membresias.Domain;

import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import com.gym.backend.Membresias.Domain.Exceptions.MembresiaValidationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Membresia {
    private Long id;
    private Long usuarioId;
    private Long planId;
    private Long pagoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoMembresia estado;
    private LocalDate fechaCreacion;
    private LocalDate fechaActualizacion;

    // Campos para QR
    private String codigoAcceso;
    private LocalDateTime codigoExpiracion;

    public void validar() {
        if (usuarioId == null)
            throw new MembresiaValidationException("El ID de usuario es requerido");
        if (planId == null)
            throw new MembresiaValidationException("El ID de plan es requerido");
        if (pagoId == null)
            throw new MembresiaValidationException("El ID de pago es requerido");
        if (fechaInicio == null)
            throw new MembresiaValidationException("La fecha de inicio es requerida");
        if (fechaFin == null)
            throw new MembresiaValidationException("La fecha de fin es requerida");
        if (fechaFin.isBefore(fechaInicio))
            throw new MembresiaValidationException("La fecha de fin no puede ser anterior a la fecha de inicio");

        LocalDate hoy = LocalDate.now();
        if (fechaInicio.isBefore(hoy.minusDays(1))) {
            throw new MembresiaValidationException("La fecha de inicio no puede ser muy antigua");
        }
    }

    public boolean estaActiva() {
        LocalDate hoy = LocalDate.now();
        return estado == EstadoMembresia.ACTIVA &&
                !fechaInicio.isAfter(hoy) &&
                !fechaFin.isBefore(hoy);
    }

    public boolean estaVencida() {
        return LocalDate.now().isAfter(fechaFin);
    }

    public boolean estaPorVencer() {
        LocalDate hoy = LocalDate.now();
        LocalDate unaSemanaAntes = fechaFin.minusDays(7);
        return estaActiva() && (hoy.isAfter(unaSemanaAntes) || hoy.isEqual(unaSemanaAntes));
    }

    public void activar() {
        this.estado = EstadoMembresia.ACTIVA;
        this.fechaActualizacion = LocalDate.now();
    }

    public void vencer() {
        this.estado = EstadoMembresia.VENCIDA;
        this.fechaActualizacion = LocalDate.now();
    }

    public void suspender() {
        this.estado = EstadoMembresia.SUSPENDIDA;
        this.fechaActualizacion = LocalDate.now();
    }

    public void cancelar() {
        this.estado = EstadoMembresia.CANCELADA;
        this.fechaActualizacion = LocalDate.now();
    }

    public long diasRestantes() {
        LocalDate hoy = LocalDate.now();
        if (fechaFin.isBefore(hoy))
            return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(hoy, fechaFin);
    }

    public long diasTranscurridos() {
        LocalDate hoy = LocalDate.now();
        if (fechaInicio.isAfter(hoy))
            return 0;
        LocalDate fechaCalculo = fechaFin.isBefore(hoy) ? fechaFin : hoy;
        return java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaCalculo);
    }

    public double porcentajeUso() {
        long totalDias = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        if (totalDias == 0)
            return 0.0;
        return (double) diasTranscurridos() / totalDias * 100;
    }
}