package com.gym.backend.Progreso.Infrastructure.Jpa;

import com.gym.backend.Progreso.Domain.Enum.TipoFoto;
import com.gym.backend.Progreso.Infrastructure.Entity.FotoProgresoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FotoProgresoJpaRepository extends JpaRepository<FotoProgresoEntity, Long> {
    List<FotoProgresoEntity> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    List<FotoProgresoEntity> findByUsuarioIdAndTipoFotoOrderByFechaDesc(Long usuarioId, TipoFoto tipoFoto);

    List<FotoProgresoEntity> findByUsuarioIdAndFechaBetweenOrderByFechaAsc(
            Long usuarioId, LocalDate inicio, LocalDate fin);

    Optional<FotoProgresoEntity> findFirstByUsuarioIdOrderByFechaDesc(Long usuarioId);

    int countByUsuarioId(Long usuarioId);
}
