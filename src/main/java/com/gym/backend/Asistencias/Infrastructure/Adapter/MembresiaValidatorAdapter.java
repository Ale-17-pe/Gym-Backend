package com.gym.backend.Asistencias.Infrastructure.Adapter;

import com.gym.backend.Asistencias.Domain.MembresiaValidatorPort;
import com.gym.backend.Membresias.Domain.Membresia;
import com.gym.backend.Membresias.Domain.MembresiaUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MembresiaValidatorAdapter implements MembresiaValidatorPort {

    private final MembresiaUseCase membresiaUseCase;

    @Override
    public boolean tieneMembresiaActiva(Long usuarioId) {
        try {
            Membresia membresia = membresiaUseCase.obtenerActivaPorUsuario(usuarioId);
            return membresia != null && membresia.estaActiva();
        } catch (Exception e) {
            log.warn("Error al validar membresía para usuario {}: {}", usuarioId, e.getMessage());
            return false;
        }
    }

    @Override
    public Membresia obtenerMembresiaActiva(Long usuarioId) {
        try {
            return membresiaUseCase.obtenerActivaPorUsuario(usuarioId);
        } catch (Exception e) {
            log.warn("Error al obtener membresía activa para usuario {}: {}", usuarioId, e.getMessage());
            return null;
        }
    }

    @Override
    public boolean puedeAccederGimnasio(Long usuarioId) {
        try {
            Membresia membresia = membresiaUseCase.obtenerActivaPorUsuario(usuarioId);
            if (membresia == null) {
                log.info("Usuario {} no tiene membresía activa", usuarioId);
                return false;
            }

            if (!membresia.estaActiva()) {
                log.info("Membresía del usuario {} no está activa", usuarioId);
                return false;
            }

            if (membresia.estaVencida()) {
                log.info("Membresía del usuario {} está vencida", usuarioId);
                return false;
            }

            log.info("Usuario {} puede acceder al gimnasio", usuarioId);
            return true;

        } catch (Exception e) {
            log.error("Error al verificar acceso para usuario {}: {}", usuarioId, e.getMessage());
            return false;
        }
    }
}