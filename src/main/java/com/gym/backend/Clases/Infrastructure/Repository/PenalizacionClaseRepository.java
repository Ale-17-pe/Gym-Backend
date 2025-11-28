package com.gym.backend.Clases.Infrastructure.Repository;

import com.gym.backend.Clases.Infrastructure.Entity.PenalizacionClaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PenalizacionClaseRepository extends JpaRepository<PenalizacionClaseEntity, Long> {

    List<PenalizacionClaseEntity> findByUsuarioIdAndActivoTrue(Long usuarioId);

    @Query("SELECT SUM(p.puntos) FROM PenalizacionClaseEntity p WHERE p.usuario.id = :usuarioId AND p.activo = true AND p.fechaPenalizacion >= :desde")
    Integer sumPuntosByUsuarioAndFechaAfter(@Param("usuarioId") Long usuarioId, @Param("desde") LocalDateTime desde);
}
