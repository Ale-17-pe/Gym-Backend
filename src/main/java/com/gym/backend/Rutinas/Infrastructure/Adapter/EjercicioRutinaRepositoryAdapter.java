package com.gym.backend.Rutinas.Infrastructure.Adapter;

import com.gym.backend.Rutinas.Domain.EjercicioRutina;
import com.gym.backend.Rutinas.Domain.EjercicioRutinaRepositoryPort;
import com.gym.backend.Rutinas.Infrastructure.Entity.DiaRutinaEntity;
import com.gym.backend.Rutinas.Infrastructure.Entity.EjercicioEntity;
import com.gym.backend.Rutinas.Infrastructure.Entity.EjercicioRutinaEntity;
import com.gym.backend.Rutinas.Infrastructure.Jpa.EjercicioRutinaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EjercicioRutinaRepositoryAdapter implements EjercicioRutinaRepositoryPort {

    private final EjercicioRutinaJpaRepository jpa;

    @Override
    public EjercicioRutina guardar(EjercicioRutina ejercicioRutina) {
        return toDomain(jpa.save(toEntity(ejercicioRutina)));
    }

    @Override
    public Optional<EjercicioRutina> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<EjercicioRutina> buscarPorDiaRutina(Long diaRutinaId) {
        return jpa.findByDiaRutinaIdOrderByOrdenAsc(diaRutinaId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    @Override
    @Transactional
    public void eliminarPorDiaRutina(Long diaRutinaId) {
        jpa.deleteByDiaRutinaId(diaRutinaId);
    }

    private EjercicioRutina toDomain(EjercicioRutinaEntity e) {
        return EjercicioRutina.builder()
                .id(e.getId())
                .diaRutinaId(e.getDiaRutina() != null ? e.getDiaRutina().getId() : null)
                .ejercicioId(e.getEjercicio() != null ? e.getEjercicio().getId() : null)
                .nombreEjercicio(e.getEjercicio() != null ? e.getEjercicio().getNombre() : null)
                .series(e.getSeries())
                .repeticiones(e.getRepeticiones())
                .rangoRepeticiones(e.getRangoRepeticiones())
                .descansoSegundos(e.getDescansoSegundos())
                .pesoSugerido(e.getPesoSugerido())
                .orden(e.getOrden())
                .notas(e.getNotas())
                .tempo(e.getTempo())
                .build();
    }

    private EjercicioRutinaEntity toEntity(EjercicioRutina er) {
        EjercicioRutinaEntity entity = EjercicioRutinaEntity.builder()
                .id(er.getId())
                .series(er.getSeries())
                .repeticiones(er.getRepeticiones())
                .rangoRepeticiones(er.getRangoRepeticiones())
                .descansoSegundos(er.getDescansoSegundos())
                .pesoSugerido(er.getPesoSugerido())
                .orden(er.getOrden())
                .notas(er.getNotas())
                .tempo(er.getTempo())
                .build();

        if (er.getDiaRutinaId() != null) {
            DiaRutinaEntity dia = new DiaRutinaEntity();
            dia.setId(er.getDiaRutinaId());
            entity.setDiaRutina(dia);
        }

        if (er.getEjercicioId() != null) {
            EjercicioEntity ejercicio = new EjercicioEntity();
            ejercicio.setId(er.getEjercicioId());
            entity.setEjercicio(ejercicio);
        }

        return entity;
    }
}
