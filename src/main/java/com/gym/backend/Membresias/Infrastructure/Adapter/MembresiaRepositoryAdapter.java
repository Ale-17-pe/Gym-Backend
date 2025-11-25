package com.gym.backend.Membresias.Infrastructure.Adapter;

import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import com.gym.backend.Membresias.Domain.Membresia;
import com.gym.backend.Membresias.Domain.MembresiaRepositoryPort;
import com.gym.backend.Membresias.Infrastructure.Entity.MembresiaEntity;
import com.gym.backend.Membresias.Infrastructure.Jpa.MembresiaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MembresiaRepositoryAdapter implements MembresiaRepositoryPort {
    private final MembresiaJpaRepository jpa;

    @Override
    public Membresia guardar(Membresia membresia) {
        return toDomain(jpa.save(toEntity(membresia)));
    }

    @Override
    public Membresia actualizar(Membresia membresia) {
        return jpa.findById(membresia.getId())
                .map(existente -> {
                    actualizarEntityDesdeDomain(existente, membresia);
                    return toDomain(jpa.save(existente));
                })
                .orElseThrow(() -> new RuntimeException("Membresía no encontrada para actualizar"));
    }

    @Override
    public Optional<Membresia> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Membresia> listar() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Membresia> listarPaginated(Pageable pageable) {
        return jpa.findAll(pageable).map(this::toDomain);
    }

    @Override
    public List<Membresia> listarPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioId(usuarioId).stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Membresia> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable) {
        return jpa.findByUsuarioId(usuarioId, pageable).map(this::toDomain);
    }

    @Override
    public Optional<Membresia> buscarActivaPorUsuario(Long usuarioId) {
        return jpa.findActivaByUsuarioId(usuarioId).map(this::toDomain);
    }

    @Override
    public List<Membresia> listarPorEstado(EstadoMembresia estado) {
        return jpa.findByEstado(estado).stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Membresia> listarPorEstadoPaginated(EstadoMembresia estado, Pageable pageable) {
        return jpa.findByEstado(estado, pageable).map(this::toDomain);
    }

    @Override
    public List<Membresia> listarPorVencer() {
        return jpa.findPorVencer().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Membresia> buscarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return jpa.findByFechaInicioBetween(fechaInicio, fechaFin)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Long contarTotal() {
        return jpa.count();
    }

    @Override
    public Long contarPorEstado(EstadoMembresia estado) {
        return jpa.countByEstado(estado);
    }

    @Override
    public Long contarPorVencer() {
        return jpa.countPorVencer();
    }

    @Override
    public Long contarPorUsuario(Long usuarioId) {
        return jpa.countByUsuarioId(usuarioId);
    }

    @Override
    public Optional<Membresia> buscarPorCodigoAcceso(String codigoAcceso) {
        return jpa.findByCodigoAcceso(codigoAcceso).map(this::toDomain);
    }

    private void actualizarEntityDesdeDomain(MembresiaEntity entity, Membresia domain) {
        entity.setFechaInicio(domain.getFechaInicio());
        entity.setFechaFin(domain.getFechaFin());
        entity.setEstado(domain.getEstado());
        entity.setFechaActualizacion(domain.getFechaActualizacion());
        entity.setCodigoAcceso(domain.getCodigoAcceso());
        entity.setCodigoExpiracion(domain.getCodigoExpiracion());
    }

    private Membresia toDomain(MembresiaEntity entity) {
        return Membresia.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId())
                .planId(entity.getPlanId())
                .pagoId(entity.getPagoId())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .estado(entity.getEstado())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .codigoAcceso(entity.getCodigoAcceso())
                .codigoExpiracion(entity.getCodigoExpiracion())
                .build();
    }

    private MembresiaEntity toEntity(Membresia domain) {
        return MembresiaEntity.builder()
                .id(domain.getId())
                .usuarioId(domain.getUsuarioId())
                .planId(domain.getPlanId())
                .pagoId(domain.getPagoId())
                .fechaInicio(domain.getFechaInicio())
                .fechaFin(domain.getFechaFin())
                .estado(domain.getEstado())
                .fechaCreacion(domain.getFechaCreacion())
                .fechaActualizacion(domain.getFechaActualizacion())
                .codigoAcceso(domain.getCodigoAcceso())
                .codigoExpiracion(domain.getCodigoExpiracion())
                .build();
    }
}