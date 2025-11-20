package com.gym.backend.HistorialPagos.Infrastructure.Jpa;

import com.gym.backend.HistorialPagos.Infrastructure.Entity.HistorialPagoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HistorialPagoJpaRepository extends JpaRepository<HistorialPagoEntity, Long> {

    List<HistorialPagoEntity> findByUsuarioId(Long usuarioId);
    Page<HistorialPagoEntity> findByUsuarioId(Long usuarioId, Pageable pageable);
    List<HistorialPagoEntity> findByPagoId(Long pagoId);
    List<HistorialPagoEntity> findByEstadoNuevo(String estado);
    List<HistorialPagoEntity> findByFechaCambioBetween(LocalDateTime inicio, LocalDateTime fin);

    Optional<HistorialPagoEntity> findTopByPagoIdOrderByFechaCambioDesc(Long pagoId);

    @Query("SELECT h FROM HistorialPagoEntity h ORDER BY h.fechaCambio DESC")
    List<HistorialPagoEntity> findTopN(Pageable pageable);
    Long countByFechaCambioBetween(LocalDateTime inicio, LocalDateTime fin);
    Long countByEstadoNuevo(String estado);

    @Query("SELECT COUNT(h) FROM HistorialPagoEntity h WHERE YEAR(h.fechaCambio) = :year AND MONTH(h.fechaCambio) = :month")
    Long countByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(h) FROM HistorialPagoEntity h WHERE h.estadoNuevo = :estado AND YEAR(h.fechaCambio) = :year AND MONTH(h.fechaCambio) = :month")
    Long countByEstadoNuevoAndYearAndMonth(@Param("estado") String estado,
                                           @Param("year") int year,
                                           @Param("month") int month);
}