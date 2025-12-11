package com.gym.backend.Fidelidad.Infrastructure.Repository;

import com.gym.backend.Fidelidad.Infrastructure.Entity.RecompensaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecompensaJpaRepository extends JpaRepository<RecompensaEntity, Long> {

    List<RecompensaEntity> findByActivoTrue();

    @Query("SELECT r FROM RecompensaEntity r WHERE r.activo = true " +
            "AND (r.stock IS NULL OR r.stock > 0) " +
            "AND (r.fechaInicio IS NULL OR r.fechaInicio <= :hoy) " +
            "AND (r.fechaFin IS NULL OR r.fechaFin >= :hoy)")
    List<RecompensaEntity> findDisponibles(LocalDate hoy);
}
