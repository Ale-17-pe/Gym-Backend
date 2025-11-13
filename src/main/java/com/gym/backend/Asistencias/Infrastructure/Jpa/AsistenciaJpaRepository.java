package com.gym.backend.Asistencias.Infrastructure.Jpa;

import com.gym.backend.Asistencias.Infrastructure.Entity.AsistenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AsistenciaJpaRepository extends JpaRepository<AsistenciaEntity, Long> {

    List<AsistenciaEntity> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndFechaHoraBetween(
            Long usuarioId,
            LocalDateTime desde,
            LocalDateTime hasta
    );
}
