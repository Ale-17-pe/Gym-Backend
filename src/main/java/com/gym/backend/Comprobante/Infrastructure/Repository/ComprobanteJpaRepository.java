package com.gym.backend.Comprobante.Infrastructure.Repository;

import com.gym.backend.Comprobante.Infrastructure.Entity.ComprobanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para Comprobantes - NORMALIZADO (3NF)
 */
@Repository
public interface ComprobanteJpaRepository extends JpaRepository<ComprobanteEntity, Long> {
    Optional<ComprobanteEntity> findByNumeroComprobante(String numeroComprobante);

    Optional<ComprobanteEntity> findByPagoId(Long pagoId);

    /**
     * Busca comprobantes por usuario haciendo JOIN con la tabla pagos.
     * NORMALIZADO 3NF: usuario_id ya no está en comprobantes, se obtiene de pagos.
     */
    @Query("SELECT c FROM ComprobanteEntity c WHERE c.pagoId IN " +
            "(SELECT p.id FROM com.gym.backend.Pago.Infrastructure.Entity.PagoEntity p WHERE p.usuarioId = :usuarioId) "
            +
            "ORDER BY c.fechaEmision DESC")
    List<ComprobanteEntity> findByUsuarioIdViaJoin(@Param("usuarioId") Long usuarioId);

    boolean existsByPagoId(Long pagoId);
}
