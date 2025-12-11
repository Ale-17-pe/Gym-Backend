package com.gym.backend.Rutinas.Infrastructure.Adapter;

import com.gym.backend.Rutinas.Domain.RegistroEntrenamiento;
import com.gym.backend.Rutinas.Domain.RegistroEntrenamientoRepositoryPort;
import com.gym.backend.Rutinas.Infrastructure.Entity.RegistroEntrenamientoEntity;
import com.gym.backend.Rutinas.Infrastructure.Jpa.RegistroEntrenamientoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegistroEntrenamientoRepositoryAdapter implements RegistroEntrenamientoRepositoryPort {

    private final RegistroEntrenamientoJpaRepository jpa;

    @Override
    public RegistroEntrenamiento guardar(RegistroEntrenamiento registro) {
        return toDomain(jpa.save(toEntity(registro)));
    }

    @Override
    public Optional<RegistroEntrenamiento> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<RegistroEntrenamiento> buscarPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioIdOrderByFechaEntrenamientoDesc(usuarioId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<RegistroEntrenamiento> buscarPorUsuarioYRango(Long usuarioId, LocalDate inicio, LocalDate fin) {
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime finDateTime = fin.plusDays(1).atStartOfDay();
        return jpa.findByUsuarioIdAndFechaEntrenamientoBetweenOrderByFechaEntrenamientoDesc(
                usuarioId, inicioDateTime, finDateTime).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<RegistroEntrenamiento> buscarPorRutina(Long rutinaId) {
        return jpa.findByRutinaIdOrderByFechaEntrenamientoDesc(rutinaId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public Optional<RegistroEntrenamiento> buscarUltimoPorUsuario(Long usuarioId) {
        return jpa.findFirstByUsuarioIdOrderByFechaEntrenamientoDesc(usuarioId)
                .map(this::toDomain);
    }

    @Override
    public int contarPorUsuarioYMes(Long usuarioId, int año, int mes) {
        return jpa.countByUsuarioIdAndMes(usuarioId, año, mes);
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    private RegistroEntrenamiento toDomain(RegistroEntrenamientoEntity e) {
        return RegistroEntrenamiento.builder()
                .id(e.getId())
                .usuarioId(e.getUsuarioId())
                .rutinaId(e.getRutinaId())
                .diaRutinaId(e.getDiaRutinaId())
                .fechaEntrenamiento(e.getFechaEntrenamiento())
                .duracionMinutos(e.getDuracionMinutos())
                .notas(e.getNotas())
                .nivelEnergia(e.getNivelEnergia())
                .nivelSatisfaccion(e.getNivelSatisfaccion())
                .completado(e.isCompletado())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    private RegistroEntrenamientoEntity toEntity(RegistroEntrenamiento r) {
        return RegistroEntrenamientoEntity.builder()
                .id(r.getId())
                .usuarioId(r.getUsuarioId())
                .rutinaId(r.getRutinaId())
                .diaRutinaId(r.getDiaRutinaId())
                .fechaEntrenamiento(r.getFechaEntrenamiento())
                .duracionMinutos(r.getDuracionMinutos())
                .notas(r.getNotas())
                .nivelEnergia(r.getNivelEnergia())
                .nivelSatisfaccion(r.getNivelSatisfaccion())
                .completado(r.isCompletado())
                .fechaCreacion(r.getFechaCreacion())
                .build();
    }
}
