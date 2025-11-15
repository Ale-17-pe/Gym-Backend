package com.gym.backend.Membresias.Infrastructure.Adapter;


import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import com.gym.backend.Membresias.Domain.Membresia;
import com.gym.backend.Membresias.Domain.MembresiaRepositoryPort;
import com.gym.backend.Membresias.Infrastructure.Entity.MembresiaEntity;
import com.gym.backend.Membresias.Infrastructure.Jpa.MembresiaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MembresiaRepositoryAdapter implements MembresiaRepositoryPort {
    private final MembresiaJpaRepository jpa;

    @Override public Membresia guardar(Membresia membresia) { return toDomain(jpa.save(toEntity(membresia))); }
    @Override public Membresia actualizar(Membresia membresia) { return toDomain(jpa.save(toEntity(membresia))); }
    @Override public Optional<Membresia> buscarPorId(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public List<Membresia> listar() { return jpa.findAll().stream().map(this::toDomain).toList(); }
    @Override public List<Membresia> listarPorUsuario(Long usuarioId) { return jpa.findByUsuarioId(usuarioId).stream().map(this::toDomain).toList(); }
    @Override public Optional<Membresia> buscarActivaPorUsuario(Long usuarioId) { return jpa.findActivaByUsuarioId(usuarioId).map(this::toDomain); }
    @Override public List<Membresia> listarPorEstado(EstadoMembresia estado) { return jpa.findByEstado(estado).stream().map(this::toDomain).toList(); }

    private Membresia toDomain(MembresiaEntity entity) {
        return Membresia.builder()
                .id(entity.getId()).usuarioId(entity.getUsuarioId()).planId(entity.getPlanId())
                .pagoId(entity.getPagoId()).fechaInicio(entity.getFechaInicio()).fechaFin(entity.getFechaFin())
                .estado(entity.getEstado()).fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion()).build();
    }

    private MembresiaEntity toEntity(Membresia domain) {
        return MembresiaEntity.builder()
                .id(domain.getId()).usuarioId(domain.getUsuarioId()).planId(domain.getPlanId())
                .pagoId(domain.getPagoId()).fechaInicio(domain.getFechaInicio()).fechaFin(domain.getFechaFin())
                .estado(domain.getEstado()).fechaCreacion(domain.getFechaCreacion())
                .fechaActualizacion(domain.getFechaActualizacion()).build();
    }
}