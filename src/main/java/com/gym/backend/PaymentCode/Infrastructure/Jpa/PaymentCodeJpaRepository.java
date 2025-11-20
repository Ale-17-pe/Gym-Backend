package com.gym.backend.PaymentCode.Infrastructure.Jpa;

import com.gym.backend.PaymentCode.Domain.Enums.EstadoPaymentCode;
import com.gym.backend.PaymentCode.Infrastructure.Entity.PaymentCodeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentCodeJpaRepository extends JpaRepository<PaymentCodeEntity, Long> {
    Optional<PaymentCodeEntity> findByPagoId(Long pagoId);
    Optional<PaymentCodeEntity> findByCodigo(String codigo);
    List<PaymentCodeEntity> findByEstado(EstadoPaymentCode estado);
    List<PaymentCodeEntity> findByFechaExpiracionBefore(LocalDateTime fecha);

    @Query("SELECT p FROM PaymentCodeEntity p WHERE p.estado = :estado AND p.fechaExpiracion < :fecha")
    List<PaymentCodeEntity> findByEstadoAndFechaExpiracionBefore(@Param("estado") EstadoPaymentCode estado,
                                                                 @Param("fecha") LocalDateTime fecha);

    @Query("SELECT p FROM PaymentCodeEntity p WHERE p.estado = :estado AND p.fechaExpiracion BETWEEN :inicio AND :fin")
    List<PaymentCodeEntity> findByEstadoAndFechaExpiracionBetween(@Param("estado") EstadoPaymentCode estado,
                                                                  @Param("inicio") LocalDateTime inicio,
                                                                  @Param("fin") LocalDateTime fin);

    Page<PaymentCodeEntity> findAll(Pageable pageable);

    Long countByEstado(EstadoPaymentCode estado);

    @Query("SELECT COUNT(p) FROM PaymentCodeEntity p WHERE p.estado = :estado AND p.fechaExpiracion BETWEEN :inicio AND :fin")
    Long countByEstadoAndFechaExpiracionBetween(@Param("estado") EstadoPaymentCode estado,
                                                @Param("inicio") LocalDateTime inicio,
                                                @Param("fin") LocalDateTime fin);

    @Modifying
    @Transactional
    @Query("DELETE FROM PaymentCodeEntity p WHERE p.fechaCreacion < :fechaLimite")
    int deleteByFechaCreacionBefore(@Param("fechaLimite") LocalDateTime fechaLimite);
}