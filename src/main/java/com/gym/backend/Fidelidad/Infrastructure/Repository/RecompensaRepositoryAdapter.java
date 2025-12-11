package com.gym.backend.Fidelidad.Infrastructure.Repository;

import com.gym.backend.Fidelidad.Domain.Recompensa;
import com.gym.backend.Fidelidad.Domain.RecompensaRepositoryPort;
import com.gym.backend.Fidelidad.Infrastructure.Entity.RecompensaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RecompensaRepositoryAdapter implements RecompensaRepositoryPort {

    private final RecompensaJpaRepository jpaRepository;

    @Override
    public Recompensa guardar(Recompensa recompensa) {
        RecompensaEntity entity = toEntity(recompensa);
        RecompensaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Recompensa> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Recompensa> listarActivas() {
        return jpaRepository.findByActivoTrue()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Recompensa> listarTodas() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Recompensa> listarDisponibles() {
        return jpaRepository.findDisponibles(LocalDate.now())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // ===== MAPPERS =====

    private Recompensa toDomain(RecompensaEntity entity) {
        return Recompensa.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .costoPuntos(entity.getCostoPuntos())
                .tipo(entity.getTipo())
                .valor(entity.getValor())
                .stock(entity.getStock())
                .activo(entity.getActivo())
                .imagenUrl(entity.getImagenUrl())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }

    private RecompensaEntity toEntity(Recompensa domain) {
        return RecompensaEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion())
                .costoPuntos(domain.getCostoPuntos())
                .tipo(domain.getTipo())
                .valor(domain.getValor())
                .stock(domain.getStock())
                .activo(domain.getActivo())
                .imagenUrl(domain.getImagenUrl())
                .fechaInicio(domain.getFechaInicio())
                .fechaFin(domain.getFechaFin())
                .fechaCreacion(domain.getFechaCreacion())
                .build();
    }
}
