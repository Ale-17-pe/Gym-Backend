package com.gym.backend.Rutinas.Infrastructure.Adapter;

import com.gym.backend.Rutinas.Domain.DiaRutina;
import com.gym.backend.Rutinas.Domain.DiaRutinaRepositoryPort;
import com.gym.backend.Rutinas.Infrastructure.Entity.DiaRutinaEntity;
import com.gym.backend.Rutinas.Infrastructure.Entity.RutinaEntity;
import com.gym.backend.Rutinas.Infrastructure.Jpa.DiaRutinaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DiaRutinaRepositoryAdapter implements DiaRutinaRepositoryPort {

    private final DiaRutinaJpaRepository jpa;

    @Override
    public DiaRutina guardar(DiaRutina diaRutina) {
        return toDomain(jpa.save(toEntity(diaRutina)));
    }

    @Override
    public Optional<DiaRutina> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<DiaRutina> buscarPorRutina(Long rutinaId) {
        return jpa.findByRutinaIdOrderByOrdenAsc(rutinaId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    @Override
    @Transactional
    public void eliminarPorRutina(Long rutinaId) {
        jpa.deleteByRutinaId(rutinaId);
    }

    private DiaRutina toDomain(DiaRutinaEntity e) {
        return DiaRutina.builder()
                .id(e.getId())
                .rutinaId(e.getRutina() != null ? e.getRutina().getId() : null)
                .diaSemana(e.getDiaSemana())
                .nombre(e.getNombre())
                .notas(e.getNotas())
                .esDescanso(e.isEsDescanso())
                .orden(e.getOrden())
                .build();
    }

    private DiaRutinaEntity toEntity(DiaRutina d) {
        DiaRutinaEntity entity = DiaRutinaEntity.builder()
                .id(d.getId())
                .diaSemana(d.getDiaSemana())
                .nombre(d.getNombre())
                .notas(d.getNotas())
                .esDescanso(d.isEsDescanso())
                .orden(d.getOrden())
                .build();

        if (d.getRutinaId() != null) {
            RutinaEntity rutina = new RutinaEntity();
            rutina.setId(d.getRutinaId());
            entity.setRutina(rutina);
        }

        return entity;
    }
}
