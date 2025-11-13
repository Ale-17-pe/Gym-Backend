package com.gym.backend.HistorialMembresias.Infrastructure.Adapter;

import com.gym.backend.HistorialMembresias.Domain.*;
import com.gym.backend.HistorialMembresias.Domain.HistorialMembresiaRepositoryPort;
import com.gym.backend.HistorialMembresias.Infrastructure.Entity.HistorialMembresiaEntity;
import com.gym.backend.HistorialMembresias.Infrastructure.Jpa.HistorialMembresiaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HistorialMembresiaRepositoryAdapter implements HistorialMembresiaRepositoryPort {

    private final HistorialMembresiaJpaRepository jpa;

    public HistorialMembresiaRepositoryAdapter(HistorialMembresiaJpaRepository jpa) {
        this.jpa = jpa;
    }

    private HistorialMembresia toDomain(HistorialMembresiaEntity e) {
        return HistorialMembresia.builder()
                .id(e.getId())
                .usuarioId(e.getUsuarioId())
                .planId(e.getPlanId())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .accion(e.getAccion())
                .estado(e.getEstado())
                .build();
    }

    private HistorialMembresiaEntity toEntity(HistorialMembresia d) {
        return HistorialMembresiaEntity.builder()
                .id(d.getId())
                .usuarioId(d.getUsuarioId())
                .planId(d.getPlanId())
                .fechaInicio(d.getFechaInicio())
                .fechaFin(d.getFechaFin())
                .accion(d.getAccion())
                .estado(d.getEstado())
                .build();
    }

    @Override
    public HistorialMembresia registrar(HistorialMembresia historial) {
        return toDomain(jpa.save(toEntity(historial)));
    }

    @Override
    public List<HistorialMembresia> listar() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<HistorialMembresia> listarPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioId(usuarioId).stream().map(this::toDomain).toList();
    }
}