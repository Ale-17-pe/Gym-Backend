package com.gym.backend.Membresias.Infrastructure.Jpa;

import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import com.gym.backend.Membresias.Infrastructure.Entity.MembresiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface MembresiaJpaRepository extends JpaRepository<MembresiaEntity, Long> {
    List<MembresiaEntity> findByUsuarioId(Long usuarioId);

    @Query("SELECT m FROM MembresiaEntity m WHERE m.usuarioId = :usuarioId AND m.estado = 'ACTIVA'")
    Optional<MembresiaEntity> findActivaByUsuarioId(@Param("usuarioId") Long usuarioId);

    List<MembresiaEntity> findByEstado(EstadoMembresia estado);
}
