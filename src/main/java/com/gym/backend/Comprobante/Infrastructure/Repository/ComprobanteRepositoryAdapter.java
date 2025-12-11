package com.gym.backend.Comprobante.Infrastructure.Repository;

import com.gym.backend.Comprobante.Application.Mapper.ComprobanteMapper;
import com.gym.backend.Comprobante.Domain.Comprobante;
import com.gym.backend.Comprobante.Domain.ComprobanteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter para repositorio de Comprobantes - NORMALIZADO (3NF)
 */
@Component
@RequiredArgsConstructor
public class ComprobanteRepositoryAdapter implements ComprobanteRepositoryPort {
    private final ComprobanteJpaRepository jpaRepository;
    private final ComprobanteMapper mapper;

    @Override
    public Comprobante guardar(Comprobante comprobante) {
        var entity = mapper.toEntity(comprobante);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Comprobante> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Comprobante> buscarPorNumero(String numeroComprobante) {
        return jpaRepository.findByNumeroComprobante(numeroComprobante).map(mapper::toDomain);
    }

    @Override
    public Optional<Comprobante> buscarPorPagoId(Long pagoId) {
        return jpaRepository.findByPagoId(pagoId).map(mapper::toDomain);
    }

    @Override
    public List<Comprobante> listarPorUsuario(Long usuarioId) {
        // NORMALIZADO 3NF: Usa JOIN con pagos para obtener usuario_id
        return jpaRepository.findByUsuarioIdViaJoin(usuarioId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comprobante> listar() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePorPagoId(Long pagoId) {
        return jpaRepository.existsByPagoId(pagoId);
    }
}
