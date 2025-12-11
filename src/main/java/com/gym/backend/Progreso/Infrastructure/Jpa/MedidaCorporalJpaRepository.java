package com.gym.backend.Progreso.Infrastructure.Jpa;

import com.gym.backend.Progreso.Infrastructure.Entity.MedidaCorporalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedidaCorporalJpaRepository extends JpaRepository<MedidaCorporalEntity, Long> {
    List<MedidaCorporalEntity> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    List<MedidaCorporalEntity> findByUsuarioIdAndFechaBetweenOrderByFechaAsc(
            Long usuarioId, LocalDate inicio, LocalDate fin);

    Optional<MedidaCorporalEntity> findFirstByUsuarioIdOrderByFechaDesc(Long usuarioId);

    Optional<MedidaCorporalEntity> findByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);
}
