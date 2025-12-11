package com.gym.backend.Fidelidad.Infrastructure.Repository;

import com.gym.backend.Fidelidad.Infrastructure.Entity.PuntosFidelidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PuntosFidelidadJpaRepository extends JpaRepository<PuntosFidelidadEntity, Long> {

    Optional<PuntosFidelidadEntity> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioId(Long usuarioId);
}
