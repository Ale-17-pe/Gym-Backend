package com.gym.backend.Asistencias.Domain;

import com.gym.backend.Membresias.Domain.Membresia;

public interface MembresiaValidatorPort {
    boolean tieneMembresiaActiva(Long usuarioId);
    Membresia obtenerMembresiaActiva(Long usuarioId);
    boolean puedeAccederGimnasio(Long usuarioId);
}