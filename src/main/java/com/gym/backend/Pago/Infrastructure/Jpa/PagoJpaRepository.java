package com.gym.backend.Pago.Infrastructure.Jpa;

import com.gym.backend.Pago.Domain.Enum.EstadoPago;
import com.gym.backend.Pago.Infrastructure.Entity.PagoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PagoJpaRepository extends JpaRepository<PagoEntity, Long> {
    List<PagoEntity> findByUsuarioId(Long usuarioId);
    Page<PagoEntity> findByUsuarioId(Long usuarioId, Pageable pageable);

    List<PagoEntity> findByEstado(EstadoPago estado);

    Optional<PagoEntity> findByReferencia(String referencia);

    List<PagoEntity> findByFechaCreacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    @Query("SELECT p FROM PagoEntity p WHERE p.estado = 'PENDIENTE'")
    List<PagoEntity> findPagosPendientes();

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoEntity p WHERE p.estado = :estado AND p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    Double sumMontoByEstadoAndFechaCreacionBetween(@Param("estado") EstadoPago estado,
                                                   @Param("fechaInicio") LocalDateTime fechaInicio,
                                                   @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COUNT(p) FROM PagoEntity p WHERE p.estado = :estado AND p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    Long countByEstadoAndFechaCreacionBetween(@Param("estado") EstadoPago estado,
                                              @Param("fechaInicio") LocalDateTime fechaInicio,
                                              @Param("fechaFin") LocalDateTime fechaFin);
}
