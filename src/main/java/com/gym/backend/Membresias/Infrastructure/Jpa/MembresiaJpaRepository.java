package com.gym.backend.Membresias.Infrastructure.Jpa;

import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import com.gym.backend.Membresias.Infrastructure.Entity.MembresiaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembresiaJpaRepository extends JpaRepository<MembresiaEntity, Long> {
    List<MembresiaEntity> findByUsuarioId(Long usuarioId);

    Page<MembresiaEntity> findByUsuarioId(Long usuarioId, Pageable pageable);

    List<MembresiaEntity> findByEstado(EstadoMembresia estado);

    Page<MembresiaEntity> findByEstado(EstadoMembresia estado, Pageable pageable);

    List<MembresiaEntity> findByFechaInicioBetween(LocalDate fechaInicio, LocalDate fechaFin);

    @Query("SELECT m FROM MembresiaEntity m WHERE m.usuarioId = :usuarioId AND m.estado = 'ACTIVA'")
    Optional<MembresiaEntity> findActivaByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT m FROM MembresiaEntity m WHERE m.estado = 'ACTIVA' AND m.fechaFin <= :fechaLimite")
    List<MembresiaEntity> findPorVencer(@Param("fechaLimite") LocalDate fechaLimite);

    @Query("SELECT COUNT(m) FROM MembresiaEntity m WHERE m.estado = 'ACTIVA' AND m.fechaFin <= :fecha")
    Long countPorVencer(@Param("fecha") LocalDate fecha);

    Long countByEstado(EstadoMembresia estado);

    Long countByUsuarioId(Long usuarioId);

    Optional<MembresiaEntity> findByCodigoAcceso(String codigoAcceso);
}