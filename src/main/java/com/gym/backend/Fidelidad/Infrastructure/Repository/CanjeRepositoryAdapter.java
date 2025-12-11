package com.gym.backend.Fidelidad.Infrastructure.Repository;

import com.gym.backend.Fidelidad.Domain.Canje;
import com.gym.backend.Fidelidad.Domain.CanjeRepositoryPort;
import com.gym.backend.Fidelidad.Domain.Enum.EstadoCanje;
import com.gym.backend.Fidelidad.Infrastructure.Entity.CanjeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CanjeRepositoryAdapter implements CanjeRepositoryPort {

    private final CanjeJpaRepository jpaRepository;

    @Override
    public Canje guardar(Canje canje) {
        CanjeEntity entity = toEntity(canje);
        CanjeEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Canje> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<Canje> buscarPorCodigo(String codigoCanje) {
        return jpaRepository.findByCodigoCanje(codigoCanje)
                .map(this::toDomain);
    }

    @Override
    public List<Canje> listarPorUsuario(Long usuarioId) {
        return jpaRepository.findByUsuarioIdOrderByFechaCanjeDesc(usuarioId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Canje> listarPorEstado(EstadoCanje estado) {
        return jpaRepository.findByEstado(estado)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Canje> listarDescuentosPendientesPorUsuario(Long usuarioId) {
        return jpaRepository.findDescuentosPendientesByUsuarioId(usuarioId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long contarPorUsuario(Long usuarioId) {
        return jpaRepository.countByUsuarioId(usuarioId);
    }

    // ===== MAPPERS =====

    private Canje toDomain(CanjeEntity entity) {
        return Canje.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId())
                .recompensaId(entity.getRecompensaId())
                .puntosUsados(entity.getPuntosUsados())
                .estado(entity.getEstado())
                .codigoCanje(entity.getCodigoCanje())
                .fechaCanje(entity.getFechaCanje())
                .fechaUso(entity.getFechaUso())
                .usadoEnPagoId(entity.getUsadoEnPagoId())
                .nombreRecompensa(entity.getNombreRecompensa())
                .descripcionRecompensa(entity.getDescripcionRecompensa())
                .build();
    }

    private CanjeEntity toEntity(Canje domain) {
        return CanjeEntity.builder()
                .id(domain.getId())
                .usuarioId(domain.getUsuarioId())
                .recompensaId(domain.getRecompensaId())
                .puntosUsados(domain.getPuntosUsados())
                .estado(domain.getEstado())
                .codigoCanje(domain.getCodigoCanje())
                .fechaCanje(domain.getFechaCanje())
                .fechaUso(domain.getFechaUso())
                .usadoEnPagoId(domain.getUsadoEnPagoId())
                .nombreRecompensa(domain.getNombreRecompensa())
                .descripcionRecompensa(domain.getDescripcionRecompensa())
                .build();
    }
}
