package com.gym.backend.Rutinas.Infrastructure.Jpa;

import com.gym.backend.Rutinas.Infrastructure.Entity.EjercicioRutinaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjercicioRutinaJpaRepository extends JpaRepository<EjercicioRutinaEntity, Long> {
    List<EjercicioRutinaEntity> findByDiaRutinaIdOrderByOrdenAsc(Long diaRutinaId);

    void deleteByDiaRutinaId(Long diaRutinaId);
}
