package com.gym.backend.Progreso.Infrastructure.Jpa;

import com.gym.backend.Progreso.Infrastructure.Entity.ObjetivoFisicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ObjetivoFisicoJpaRepository extends JpaRepository<ObjetivoFisicoEntity, Long> {
    List<ObjetivoFisicoEntity> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    Optional<ObjetivoFisicoEntity> findByUsuarioIdAndActivoTrue(Long usuarioId);

    List<ObjetivoFisicoEntity> findByUsuarioIdAndCompletadoTrueOrderByFechaCompletadoDesc(Long usuarioId);
}
