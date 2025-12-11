package com.gym.backend.Progreso.Infrastructure.Adapter;

import com.gym.backend.Progreso.Domain.ObjetivoFisico;
import com.gym.backend.Progreso.Domain.ObjetivoFisicoRepositoryPort;
import com.gym.backend.Progreso.Infrastructure.Entity.ObjetivoFisicoEntity;
import com.gym.backend.Progreso.Infrastructure.Jpa.ObjetivoFisicoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ObjetivoFisicoRepositoryAdapter implements ObjetivoFisicoRepositoryPort {

    private final ObjetivoFisicoJpaRepository jpa;

    @Override
    public ObjetivoFisico guardar(ObjetivoFisico objetivo) {
        return toDomain(jpa.save(toEntity(objetivo)));
    }

    @Override
    public Optional<ObjetivoFisico> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<ObjetivoFisico> buscarPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public Optional<ObjetivoFisico> buscarActivoPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioIdAndActivoTrue(usuarioId).map(this::toDomain);
    }

    @Override
    public List<ObjetivoFisico> buscarCompletadosPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioIdAndCompletadoTrueOrderByFechaCompletadoDesc(usuarioId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    private ObjetivoFisico toDomain(ObjetivoFisicoEntity e) {
        return ObjetivoFisico.builder()
                .id(e.getId())
                .usuarioId(e.getUsuarioId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .pesoActual(e.getPesoActual())
                .pesoObjetivo(e.getPesoObjetivo())
                .cinturaObjetivo(e.getCinturaObjetivo())
                .porcentajeGrasaObjetivo(e.getPorcentajeGrasaObjetivo())
                .fechaInicio(e.getFechaInicio())
                .fechaObjetivo(e.getFechaObjetivo())
                .activo(e.isActivo())
                .completado(e.isCompletado())
                .fechaCompletado(e.getFechaCompletado())
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    private ObjetivoFisicoEntity toEntity(ObjetivoFisico o) {
        return ObjetivoFisicoEntity.builder()
                .id(o.getId())
                .usuarioId(o.getUsuarioId())
                .nombre(o.getNombre())
                .descripcion(o.getDescripcion())
                .pesoActual(o.getPesoActual())
                .pesoObjetivo(o.getPesoObjetivo())
                .cinturaObjetivo(o.getCinturaObjetivo())
                .porcentajeGrasaObjetivo(o.getPorcentajeGrasaObjetivo())
                .fechaInicio(o.getFechaInicio())
                .fechaObjetivo(o.getFechaObjetivo())
                .activo(o.isActivo())
                .completado(o.isCompletado())
                .fechaCompletado(o.getFechaCompletado())
                .fechaCreacion(o.getFechaCreacion())
                .fechaActualizacion(o.getFechaActualizacion())
                .build();
    }
}
