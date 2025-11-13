package com.gym.backend.Membresias.Infrastructure.Adapter;


import com.gym.backend.Membresias.Domain.Membresia;
import com.gym.backend.Membresias.Domain.MembresiaRepositoryPort;
import com.gym.backend.Membresias.Infrastructure.Entity.MembresiaEntity;
import com.gym.backend.Membresias.Infrastructure.Jpa.MembresiaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MembresiaRepositoryAdapter implements MembresiaRepositoryPort {

    private final MembresiaJpaRepository jpa;

    public MembresiaRepositoryAdapter(MembresiaJpaRepository jpa) {
        this.jpa = jpa;
    }

    private Membresia toDomain(MembresiaEntity e) {
        return Membresia.builder()
                .id(e.getId())
                .usuarioId(e.getUsuarioId())
                .planId(e.getPlanId())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .estado(e.getEstado())
                .build();
    }

    private MembresiaEntity toEntity(Membresia d) {
        return MembresiaEntity.builder()
                .id(d.getId())
                .usuarioId(d.getUsuarioId())
                .planId(d.getPlanId())
                .fechaInicio(d.getFechaInicio())
                .fechaFin(d.getFechaFin())
                .estado(d.getEstado())
                .build();
    }

    @Override
    public Membresia crear(Membresia membresia) {
        return toDomain(jpa.save(toEntity(membresia)));
    }

    @Override
    public Membresia actualizar(Long id, Membresia membresia) {
        var e = jpa.findById(id).orElseThrow();

        e.setPlanId(membresia.getPlanId());
        e.setFechaInicio(membresia.getFechaInicio());
        e.setFechaFin(membresia.getFechaFin());
        e.setEstado(membresia.getEstado());

        return toDomain(jpa.save(e));
    }

    @Override
    public Membresia obtenerPorId(Long id) {
        return jpa.findById(id)
                .map(this::toDomain)
                .orElseThrow();
    }

    @Override
    public List<Membresia> listar() {
        return jpa.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Membresia> buscarPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioId(usuarioId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Membresia buscarActivaPorUsuario(Long usuarioId) {
        var entity = jpa.findByUsuarioIdAndEstado(usuarioId, "ACTIVA");
        return entity != null ? toDomain(entity) : null;
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }
}
