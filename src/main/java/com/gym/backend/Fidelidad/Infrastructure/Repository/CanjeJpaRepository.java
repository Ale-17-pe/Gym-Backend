package com.gym.backend.Fidelidad.Infrastructure.Repository;

import com.gym.backend.Fidelidad.Domain.Enum.EstadoCanje;
import com.gym.backend.Fidelidad.Infrastructure.Entity.CanjeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CanjeJpaRepository extends JpaRepository<CanjeEntity, Long> {

    Optional<CanjeEntity> findByCodigoCanje(String codigoCanje);

    List<CanjeEntity> findByUsuarioIdOrderByFechaCanjeDesc(Long usuarioId);

    List<CanjeEntity> findByEstado(EstadoCanje estado);

    @Query("SELECT c FROM CanjeEntity c JOIN RecompensaEntity r ON c.recompensaId = r.id " +
            "WHERE c.usuarioId = :usuarioId AND c.estado = 'PENDIENTE' AND r.tipo = 'DESCUENTO'")
    List<CanjeEntity> findDescuentosPendientesByUsuarioId(@Param("usuarioId") Long usuarioId);

    long countByUsuarioId(Long usuarioId);
}
