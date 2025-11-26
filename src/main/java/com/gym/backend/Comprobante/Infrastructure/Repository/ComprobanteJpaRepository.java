package com.gym.backend.Comprobante.Infrastructure.Repository;

import com.gym.backend.Comprobante.Infrastructure.Entity.ComprobanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComprobanteJpaRepository extends JpaRepository<ComprobanteEntity, Long> {
    Optional<ComprobanteEntity> findByNumeroComprobante(String numeroComprobante);

    Optional<ComprobanteEntity> findByPagoId(Long pagoId);

    List<ComprobanteEntity> findByUsuarioIdOrderByFechaEmisionDesc(Long usuarioId);

    boolean existsByPagoId(Long pagoId);
}
