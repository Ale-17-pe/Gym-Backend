package com.gym.backend.Referidos.Infrastructure.Repository;

import com.gym.backend.Referidos.Domain.Referido.EstadoReferido;
import com.gym.backend.Referidos.Infrastructure.Entity.ReferidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferidoJpaRepository extends JpaRepository<ReferidoEntity, Long> {

    Optional<ReferidoEntity> findByReferidoId(Long referidoId);

    List<ReferidoEntity> findByReferidorIdOrderByFechaReferidoDesc(Long referidorId);

    long countByReferidorIdAndEstado(Long referidorId, EstadoReferido estado);

    boolean existsByReferidoIdAndEstado(Long referidoId, EstadoReferido estado);
}
