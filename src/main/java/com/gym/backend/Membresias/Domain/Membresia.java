package com.gym.backend.Membresias.Domain;

import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import com.gym.backend.Membresias.Domain.Exceptions.MembresiaValidationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
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

    public void validar() {
        if (usuarioId == null) throw new MembresiaValidationException("El ID de usuario es requerido");
        if (planId == null) throw new MembresiaValidationException("El ID de plan es requerido");
        if (pagoId == null) throw new MembresiaValidationException("El ID de pago es requerido");
        if (fechaInicio == null) throw new MembresiaValidationException("La fecha de inicio es requerida");
        if (fechaFin == null) throw new MembresiaValidationException("La fecha de fin es requerida");
        if (fechaFin.isBefore(fechaInicio)) throw new MembresiaValidationException("La fecha de fin no puede ser anterior a la fecha de inicio");
    }

    public boolean estaActiva() {
        LocalDate hoy = LocalDate.now();
        return estado == EstadoMembresia.ACTIVA && !fechaInicio.isAfter(hoy) && !fechaFin.isBefore(hoy);
    }

    public boolean estaVencida() { return LocalDate.now().isAfter(fechaFin); }
    public void activar() { this.estado = EstadoMembresia.ACTIVA; }
    public void vencer() { this.estado = EstadoMembresia.VENCIDA; }
    public long diasRestantes() {
        LocalDate hoy = LocalDate.now();
        if (fechaFin.isBefore(hoy)) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(hoy, fechaFin);
    }
}