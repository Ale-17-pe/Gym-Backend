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

public interface HistorialMembresiaJpaRepository extends JpaRepository<HistorialMembresiaEntity, Long> {

    List<HistorialMembresiaEntity> findByUsuarioId(Long usuarioId);
    Page<HistorialMembresiaEntity> findByUsuarioId(Long usuarioId, Pageable pageable);
    List<HistorialMembresiaEntity> findByMembresiaId(Long membresiaId);
    List<HistorialMembresiaEntity> findByAccion(String accion);
    List<HistorialMembresiaEntity> findByFechaCambioBetween(LocalDateTime inicio, LocalDateTime fin);

    Optional<HistorialMembresiaEntity> findTopByMembresiaIdOrderByFechaCambioDesc(Long membresiaId);

    @Query("SELECT h FROM HistorialMembresiaEntity h ORDER BY h.fechaCambio DESC LIMIT :limite")
    List<HistorialMembresiaEntity> findTopNByOrderByFechaCambioDesc(@Param("limite") int limite);

    Long countByFechaCambioBetween(LocalDateTime inicio, LocalDateTime fin);
    Long countByAccion(String accion);

    @Query("SELECT COUNT(h) FROM HistorialMembresiaEntity h WHERE YEAR(h.fechaCambio) = :year AND MONTH(h.fechaCambio) = :month")
    Long countByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(h) FROM HistorialMembresiaEntity h WHERE h.accion = :accion AND YEAR(h.fechaCambio) = :year AND MONTH(h.fechaCambio) = :month")
    Long countByAccionAndYearAndMonth(@Param("accion") String accion,
                                      @Param("year") int year,
                                      @Param("month") int month);
}