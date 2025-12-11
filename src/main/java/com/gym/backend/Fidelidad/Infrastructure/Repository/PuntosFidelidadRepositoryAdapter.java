package com.gym.backend.Fidelidad.Infrastructure.Repository;

import com.gym.backend.Fidelidad.Domain.PuntosFidelidad;
import com.gym.backend.Fidelidad.Domain.PuntosFidelidadRepositoryPort;
import com.gym.backend.Fidelidad.Infrastructure.Entity.PuntosFidelidadEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PuntosFidelidadRepositoryAdapter implements PuntosFidelidadRepositoryPort {

    private final PuntosFidelidadJpaRepository jpaRepository;

    @Override
    public PuntosFidelidad guardar(PuntosFidelidad puntos) {
        PuntosFidelidadEntity entity = toEntity(puntos);
        PuntosFidelidadEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PuntosFidelidad> buscarPorUsuarioId(Long usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId)
                .map(this::toDomain);
    }

    @Override
    public boolean existePorUsuarioId(Long usuarioId) {
        return jpaRepository.existsByUsuarioId(usuarioId);
    }

    // ===== MAPPERS =====

    private PuntosFidelidad toDomain(PuntosFidelidadEntity entity) {
        return PuntosFidelidad.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId())
                .puntosTotales(entity.getPuntosTotales())
                .puntosDisponibles(entity.getPuntosDisponibles())
                .puntosCanjeados(entity.getPuntosCanjeados())
                .nivel(entity.getNivel())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private PuntosFidelidadEntity toEntity(PuntosFidelidad domain) {
        return PuntosFidelidadEntity.builder()
                .id(domain.getId())
                .usuarioId(domain.getUsuarioId())
                .puntosTotales(domain.getPuntosTotales())
                .puntosDisponibles(domain.getPuntosDisponibles())
                .puntosCanjeados(domain.getPuntosCanjeados())
                .nivel(domain.getNivel())
                .fechaCreacion(domain.getFechaCreacion())
                .fechaActualizacion(domain.getFechaActualizacion())
                .build();
    }
}
