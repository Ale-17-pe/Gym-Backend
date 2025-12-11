package com.gym.backend.Rutinas.Infrastructure.Adapter;

import com.gym.backend.Rutinas.Domain.Rutina;
import com.gym.backend.Rutinas.Domain.RutinaRepositoryPort;
import com.gym.backend.Rutinas.Infrastructure.Entity.RutinaEntity;
import com.gym.backend.Rutinas.Infrastructure.Jpa.RutinaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RutinaRepositoryAdapter implements RutinaRepositoryPort {

    private final RutinaJpaRepository jpa;

    @Override
    public Rutina guardar(Rutina rutina) {
        return toDomain(jpa.save(toEntity(rutina)));
    }

    @Override
    public Optional<Rutina> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Rutina> buscarPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public Optional<Rutina> buscarActivaPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioIdAndActivaTrue(usuarioId).map(this::toDomain);
    }

    @Override
    public List<Rutina> listar() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    @Override
    @Transactional
    public void desactivarTodasPorUsuario(Long usuarioId) {
        jpa.desactivarTodasPorUsuario(usuarioId);
    }

    private Rutina toDomain(RutinaEntity e) {
        return Rutina.builder()
                .id(e.getId())
                .usuarioId(e.getUsuarioId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .objetivo(e.getObjetivo())
                .duracionSemanas(e.getDuracionSemanas())
                .activa(e.isActiva())
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    private RutinaEntity toEntity(Rutina r) {
        return RutinaEntity.builder()
                .id(r.getId())
                .usuarioId(r.getUsuarioId())
                .nombre(r.getNombre())
                .descripcion(r.getDescripcion())
                .objetivo(r.getObjetivo())
                .duracionSemanas(r.getDuracionSemanas())
                .activa(r.isActiva())
                .fechaCreacion(r.getFechaCreacion())
                .fechaActualizacion(r.getFechaActualizacion())
                .build();
    }
}
