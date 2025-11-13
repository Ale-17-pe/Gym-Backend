package com.gym.backend.Asistencias.Infrastructure.Adapter;


import com.gym.backend.Asistencias.Domain.Asistencia;
import com.gym.backend.Asistencias.Domain.AsistenciaRepositoryPort;
import com.gym.backend.Asistencias.Infrastructure.Entity.AsistenciaEntity;
import com.gym.backend.Asistencias.Infrastructure.Jpa.AsistenciaJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AsistenciaRepositoryAdapter implements AsistenciaRepositoryPort {

    private final AsistenciaJpaRepository jpa;

    public AsistenciaRepositoryAdapter(AsistenciaJpaRepository jpa) {
        this.jpa = jpa;
    }

    private Asistencia toDomain(AsistenciaEntity e) {
        return Asistencia.builder()
                .id(e.getId())
                .usuarioId(e.getUsuarioId())
                .fechaHora(e.getFechaHora())
                .build();
    }

    private AsistenciaEntity toEntity(Asistencia d) {
        return AsistenciaEntity.builder()
                .id(d.getId())
                .usuarioId(d.getUsuarioId())
                .fechaHora(d.getFechaHora())
                .build();
    }

    @Override
    public Asistencia registrar(Asistencia asistencia) {
        return toDomain(jpa.save(toEntity(asistencia)));
    }

    @Override
    public List<Asistencia> listar() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Asistencia> listarPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioId(usuarioId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public boolean existeAsistenciaHoy(Long usuarioId, LocalDateTime desde, LocalDateTime hasta) {
        return jpa.existsByUsuarioIdAndFechaHoraBetween(usuarioId, desde, hasta);
    }
}
