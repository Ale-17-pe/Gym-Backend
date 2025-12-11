package com.gym.backend.Rutinas.Infrastructure.Jpa;

import com.gym.backend.Rutinas.Infrastructure.Entity.RegistroEntrenamientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroEntrenamientoJpaRepository extends JpaRepository<RegistroEntrenamientoEntity, Long> {
    List<RegistroEntrenamientoEntity> findByUsuarioIdOrderByFechaEntrenamientoDesc(Long usuarioId);

    List<RegistroEntrenamientoEntity> findByUsuarioIdAndFechaEntrenamientoBetweenOrderByFechaEntrenamientoDesc(
            Long usuarioId, LocalDateTime inicio, LocalDateTime fin);

    List<RegistroEntrenamientoEntity> findByRutinaIdOrderByFechaEntrenamientoDesc(Long rutinaId);

    Optional<RegistroEntrenamientoEntity> findFirstByUsuarioIdOrderByFechaEntrenamientoDesc(Long usuarioId);

    @Query("SELECT COUNT(r) FROM RegistroEntrenamientoEntity r WHERE r.usuarioId = :usuarioId " +
            "AND YEAR(r.fechaEntrenamiento) = :año AND MONTH(r.fechaEntrenamiento) = :mes")
    int countByUsuarioIdAndMes(@Param("usuarioId") Long usuarioId,
            @Param("año") int año,
            @Param("mes") int mes);
}
