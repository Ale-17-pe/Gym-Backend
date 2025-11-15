package com.gym.backend.Pago.Infrastructure.Jpa;

import com.gym.backend.Pago.Domain.Enum.EstadoPago;
import com.gym.backend.Pago.Infrastructure.Entity.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PagoJpaRepository extends JpaRepository<PagoEntity, Long> {
    List<PagoEntity> findByUsuarioId(Long usuarioId);

    @Query("SELECT p FROM PagoEntity p WHERE p.estado = :estado")
    List<PagoEntity> findByEstado(@Param("estado") EstadoPago estado);

    @Query("SELECT p FROM PagoEntity p WHERE p.estado = 'PENDIENTE'")
    List<PagoEntity> findPagosPendientes();
}

