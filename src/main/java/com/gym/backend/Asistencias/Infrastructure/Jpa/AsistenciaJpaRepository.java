package com.gym.backend.Asistencias.Infrastructure.Jpa;

import com.gym.backend.Asistencias.Infrastructure.Entity.AsistenciaEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.*;
import java.util.*;

public interface AsistenciaJpaRepository extends JpaRepository<AsistenciaEntity, Long> {

    List<AsistenciaEntity> findByUsuarioId(Long usuarioId);

    Page<AsistenciaEntity> findByUsuarioId(Long usuarioId, Pageable pageable);

    List<AsistenciaEntity> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    boolean existsByUsuarioIdAndFechaHoraBetween(Long usuarioId, LocalDateTime desde, LocalDateTime hasta);

    boolean existsByUsuarioIdAndTipoAndFechaHoraBetween(Long usuarioId, String tipo, LocalDateTime desde,
            LocalDateTime hasta);

    Optional<AsistenciaEntity> findByUsuarioIdAndTipoAndFechaHoraBetween(Long usuarioId, String tipo,
            LocalDateTime desde, LocalDateTime hasta);

    Long countByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT COUNT(DISTINCT a.usuarioId) FROM AsistenciaEntity a WHERE a.fechaHora BETWEEN :inicio AND :fin")
    Long countDistinctUsuarioIdByFechaHoraBetween(@Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(a) FROM AsistenciaEntity a WHERE YEAR(a.fechaHora) = :year AND MONTH(a.fechaHora) = :month AND a.tipo = :tipo")
    Long countByTipoAndYearAndMonth(@Param("tipo") String tipo,
            @Param("year") int year,
            @Param("month") int month);

    @Query("SELECT COUNT(DISTINCT a.usuarioId) FROM AsistenciaEntity a WHERE YEAR(a.fechaHora) = :year AND MONTH(a.fechaHora) = :month")
    Long countDistinctUsuarioIdByYearAndMonth(@Param("year") int year,
            @Param("month") int month);

    @Query("SELECT COUNT(a) FROM AsistenciaEntity a WHERE a.usuarioId = :usuarioId AND YEAR(a.fechaHora) = :year AND MONTH(a.fechaHora) = :month")
    Integer countByUsuarioIdAndYearAndMonth(@Param("usuarioId") Long usuarioId,
            @Param("year") int year,
            @Param("month") int month);

    @Query("SELECT COUNT(a) FROM AsistenciaEntity a WHERE YEAR(a.fechaHora) = YEAR(CURRENT_DATE) GROUP BY MONTH(a.fechaHora)")
    List<Long> obtenerConteosMensuales();

}