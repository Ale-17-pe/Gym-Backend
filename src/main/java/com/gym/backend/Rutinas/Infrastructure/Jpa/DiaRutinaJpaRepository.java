package com.gym.backend.Rutinas.Infrastructure.Jpa;

import com.gym.backend.Rutinas.Infrastructure.Entity.DiaRutinaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaRutinaJpaRepository extends JpaRepository<DiaRutinaEntity, Long> {
    List<DiaRutinaEntity> findByRutinaIdOrderByOrdenAsc(Long rutinaId);

    void deleteByRutinaId(Long rutinaId);
}
