package com.gym.backend.Asistencias.Infrastructure.Jpa;

import com.gym.backend.Asistencias.Infrastructure.Entity.AsistenciaEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.*;
import java.util.*;

/**
 * Repositorio JPA para Asistencias - NORMALIZADO (3NF)
 * Las consultas por usuario_id ahora requieren JOIN con membresias.
 */
public interface AsistenciaJpaRepository extends JpaRepository<AsistenciaEntity, Long> {

        // NORMALIZADO 3NF: Buscar asistencias por usuario_id vía JOIN con membresias
        @Query("SELECT a FROM AsistenciaEntity a JOIN MembresiaEntity m ON a.membresiaId = m.id WHERE m.usuarioId = :usuarioId")
        List<AsistenciaEntity> findByUsuarioIdViaJoin(@Param("usuarioId") Long usuarioId);

        @Query("SELECT a FROM AsistenciaEntity a JOIN MembresiaEntity m ON a.membresiaId = m.id WHERE m.usuarioId = :usuarioId")
        Page<AsistenciaEntity> findByUsuarioIdViaJoin(@Param("usuarioId") Long usuarioId, Pageable pageable);

        List<AsistenciaEntity> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

        @Query("SELECT a FROM AsistenciaEntity a JOIN MembresiaEntity m ON a.membresiaId = m.id " +
                        "WHERE m.usuarioId = :usuarioId AND a.fechaHora BETWEEN :inicio AND :fin")
        List<AsistenciaEntity> findByUsuarioIdAndFechaHoraBetween(@Param("usuarioId") Long usuarioId,
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);

        // NORMALIZADO 3NF: Verificar existencia por usuario vía JOIN
        @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AsistenciaEntity a " +
                        "JOIN MembresiaEntity m ON a.membresiaId = m.id " +
                        "WHERE m.usuarioId = :usuarioId AND a.fechaHora BETWEEN :desde AND :hasta")
        boolean existsByUsuarioIdViaJoin(@Param("usuarioId") Long usuarioId,
                        @Param("desde") LocalDateTime desde,
                        @Param("hasta") LocalDateTime hasta);

        @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AsistenciaEntity a " +
                        "JOIN MembresiaEntity m ON a.membresiaId = m.id " +
                        "WHERE m.usuarioId = :usuarioId AND a.tipo = :tipo AND a.fechaHora BETWEEN :desde AND :hasta")
        boolean existsByUsuarioIdAndTipoViaJoin(@Param("usuarioId") Long usuarioId,
                        @Param("tipo") String tipo,
                        @Param("desde") LocalDateTime desde,
                        @Param("hasta") LocalDateTime hasta);

        @Query("SELECT a FROM AsistenciaEntity a " +
                        "JOIN MembresiaEntity m ON a.membresiaId = m.id " +
                        "WHERE m.usuarioId = :usuarioId AND a.tipo = :tipo AND a.fechaHora BETWEEN :desde AND :hasta")
        Optional<AsistenciaEntity> findByUsuarioIdAndTipoViaJoin(@Param("usuarioId") Long usuarioId,
                        @Param("tipo") String tipo,
                        @Param("desde") LocalDateTime desde,
                        @Param("hasta") LocalDateTime hasta);

        Long countByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

        @Query("SELECT COUNT(DISTINCT m.usuarioId) FROM AsistenciaEntity a " +
                        "JOIN MembresiaEntity m ON a.membresiaId = m.id " +
                        "WHERE a.fechaHora BETWEEN :inicio AND :fin")
        Long countDistinctUsuarioIdByFechaHoraBetweenViaJoin(@Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);

        @Query("SELECT COUNT(a) FROM AsistenciaEntity a WHERE YEAR(a.fechaHora) = :year AND MONTH(a.fechaHora) = :month AND a.tipo = :tipo")
        Long countByTipoAndYearAndMonth(@Param("tipo") String tipo,
                        @Param("year") int year,
                        @Param("month") int month);

        @Query("SELECT COUNT(DISTINCT m.usuarioId) FROM AsistenciaEntity a " +
                        "JOIN MembresiaEntity m ON a.membresiaId = m.id " +
                        "WHERE YEAR(a.fechaHora) = :year AND MONTH(a.fechaHora) = :month")
        Long countDistinctUsuarioIdByYearAndMonthViaJoin(@Param("year") int year,
                        @Param("month") int month);

        @Query("SELECT COUNT(a) FROM AsistenciaEntity a " +
                        "JOIN MembresiaEntity m ON a.membresiaId = m.id " +
                        "WHERE m.usuarioId = :usuarioId AND YEAR(a.fechaHora) = :year AND MONTH(a.fechaHora) = :month")
        Integer countByUsuarioIdAndYearAndMonthViaJoin(@Param("usuarioId") Long usuarioId,
                        @Param("year") int year,
                        @Param("month") int month);

        @Query("SELECT COUNT(a) FROM AsistenciaEntity a WHERE YEAR(a.fechaHora) = YEAR(CURRENT_DATE) GROUP BY MONTH(a.fechaHora)")
        List<Long> obtenerConteosMensuales();
}