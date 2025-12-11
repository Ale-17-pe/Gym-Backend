package com.gym.backend.HistorialMembresias.Infrastructure.Jpa;

import com.gym.backend.HistorialMembresias.Infrastructure.Entity.HistorialMembresiaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para HistorialMembresias - NORMALIZADO (3NF)
 * Las consultas por usuario_id ahora requieren JOIN con membresias.
 */
public interface HistorialMembresiaJpaRepository extends JpaRepository<HistorialMembresiaEntity, Long> {

    // NORMALIZADO 3NF: Buscar historial por usuario_id vía JOIN con membresias
    @Query("SELECT h FROM HistorialMembresiaEntity h " +
            "JOIN MembresiaEntity m ON h.membresiaId = m.id " +
            "WHERE m.usuarioId = :usuarioId")
    List<HistorialMembresiaEntity> findByUsuarioIdViaJoin(@Param("usuarioId") Long usuarioId);

    @Query("SELECT h FROM HistorialMembresiaEntity h " +
            "JOIN MembresiaEntity m ON h.membresiaId = m.id " +
            "WHERE m.usuarioId = :usuarioId")
    Page<HistorialMembresiaEntity> findByUsuarioIdViaJoin(@Param("usuarioId") Long usuarioId, Pageable pageable);

    List<HistorialMembresiaEntity> findByMembresiaId(Long membresiaId);

    List<HistorialMembresiaEntity> findByAccion(String accion);

    List<HistorialMembresiaEntity> findByFechaCambioBetween(LocalDateTime inicio, LocalDateTime fin);

    Optional<HistorialMembresiaEntity> findTopByMembresiaIdOrderByFechaCambioDesc(Long membresiaId);

    @Query("SELECT h FROM HistorialMembresiaEntity h ORDER BY h.fechaCambio DESC")
    List<HistorialMembresiaEntity> findTopN(Pageable pageable);

    Long countByFechaCambioBetween(LocalDateTime inicio, LocalDateTime fin);

    Long countByAccion(String accion);

    @Query("SELECT COUNT(h) FROM HistorialMembresiaEntity h WHERE YEAR(h.fechaCambio) = :year AND MONTH(h.fechaCambio) = :month")
    Long countByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(h) FROM HistorialMembresiaEntity h WHERE h.accion = :accion AND YEAR(h.fechaCambio) = :year AND MONTH(h.fechaCambio) = :month")
    Long countByAccionAndYearAndMonth(@Param("accion") String accion,
            @Param("year") int year,
            @Param("month") int month);
}