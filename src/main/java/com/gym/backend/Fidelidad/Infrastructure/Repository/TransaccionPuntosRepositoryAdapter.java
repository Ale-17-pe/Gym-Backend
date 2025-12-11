package com.gym.backend.Fidelidad.Infrastructure.Repository;

import com.gym.backend.Fidelidad.Domain.Enum.MotivoGanancia;
import com.gym.backend.Fidelidad.Domain.Enum.TipoTransaccion;
import com.gym.backend.Fidelidad.Domain.TransaccionPuntos;
import com.gym.backend.Fidelidad.Domain.TransaccionPuntosRepositoryPort;
import com.gym.backend.Fidelidad.Infrastructure.Entity.TransaccionPuntosEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TransaccionPuntosRepositoryAdapter implements TransaccionPuntosRepositoryPort {

    private final TransaccionPuntosJpaRepository jpaRepository;

    @Override
    public TransaccionPuntos guardar(TransaccionPuntos transaccion) {
        TransaccionPuntosEntity entity = toEntity(transaccion);
        TransaccionPuntosEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<TransaccionPuntos> listarPorUsuario(Long usuarioId) {
        return jpaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransaccionPuntos> listarPorUsuarioPaginado(Long usuarioId, int pagina, int tamano) {
        PageRequest pageRequest = PageRequest.of(pagina, tamano);
        return jpaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId, pageRequest)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePorReferenciaYMotivo(Long referenciaId, String tipoReferencia, MotivoGanancia motivo) {
        return jpaRepository.existsByReferenciaIdAndTipoReferenciaAndMotivo(referenciaId, tipoReferencia, motivo);
    }

    @Override
    public long contarPorUsuarioYMotivoDesde(Long usuarioId, MotivoGanancia motivo, LocalDateTime desde) {
        return jpaRepository.countByUsuarioIdAndMotivoAndFechaAfter(usuarioId, motivo, desde);
    }

    @Override
    public int sumarPuntosGanadosPorUsuario(Long usuarioId) {
        Integer suma = jpaRepository.sumPuntosByUsuarioIdAndTipo(usuarioId, TipoTransaccion.GANANCIA);
        return suma != null ? suma : 0;
    }

    // ===== MAPPERS =====

    private TransaccionPuntos toDomain(TransaccionPuntosEntity entity) {
        return TransaccionPuntos.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId())
                .tipo(entity.getTipo())
                .motivo(entity.getMotivo())
                .puntos(entity.getPuntos())
                .descripcion(entity.getDescripcion())
                .referenciaId(entity.getReferenciaId())
                .tipoReferencia(entity.getTipoReferencia())
                .fecha(entity.getFecha())
                .build();
    }

    private TransaccionPuntosEntity toEntity(TransaccionPuntos domain) {
        return TransaccionPuntosEntity.builder()
                .id(domain.getId())
                .usuarioId(domain.getUsuarioId())
                .tipo(domain.getTipo())
                .motivo(domain.getMotivo())
                .puntos(domain.getPuntos())
                .descripcion(domain.getDescripcion())
                .referenciaId(domain.getReferenciaId())
                .tipoReferencia(domain.getTipoReferencia())
                .fecha(domain.getFecha())
                .build();
    }
}
