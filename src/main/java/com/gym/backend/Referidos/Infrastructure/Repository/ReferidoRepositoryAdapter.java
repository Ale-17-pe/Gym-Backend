package com.gym.backend.Referidos.Infrastructure.Repository;

import com.gym.backend.Referidos.Domain.Referido;
import com.gym.backend.Referidos.Domain.Referido.EstadoReferido;
import com.gym.backend.Referidos.Domain.ReferidoRepositoryPort;
import com.gym.backend.Referidos.Infrastructure.Entity.ReferidoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReferidoRepositoryAdapter implements ReferidoRepositoryPort {

    private final ReferidoJpaRepository jpaRepository;

    @Override
    public Referido guardar(Referido referido) {
        ReferidoEntity entity = toEntity(referido);
        ReferidoEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Referido> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Referido> buscarPorReferidoId(Long referidoId) {
        return jpaRepository.findByReferidoId(referidoId).map(this::toDomain);
    }

    @Override
    public List<Referido> listarPorReferidorId(Long referidorId) {
        return jpaRepository.findByReferidorIdOrderByFechaReferidoDesc(referidorId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long contarReferidosCompletados(Long referidorId) {
        return jpaRepository.countByReferidorIdAndEstado(referidorId, EstadoReferido.COMPLETADO);
    }

    @Override
    public boolean existeReferidoPendiente(Long referidoId) {
        return jpaRepository.existsByReferidoIdAndEstado(referidoId, EstadoReferido.PENDIENTE);
    }

    private Referido toDomain(ReferidoEntity entity) {
        return Referido.builder()
                .id(entity.getId())
                .referidorId(entity.getReferidorId())
                .referidoId(entity.getReferidoId())
                .codigoReferido(entity.getCodigoReferido())
                .estado(entity.getEstado())
                .fechaReferido(entity.getFechaReferido())
                .fechaCompletado(entity.getFechaCompletado())
                .puntosOtorgados(entity.getPuntosOtorgados())
                .build();
    }

    private ReferidoEntity toEntity(Referido domain) {
        return ReferidoEntity.builder()
                .id(domain.getId())
                .referidorId(domain.getReferidorId())
                .referidoId(domain.getReferidoId())
                .codigoReferido(domain.getCodigoReferido())
                .estado(domain.getEstado())
                .fechaReferido(domain.getFechaReferido())
                .fechaCompletado(domain.getFechaCompletado())
                .puntosOtorgados(domain.getPuntosOtorgados())
                .build();
    }
}
