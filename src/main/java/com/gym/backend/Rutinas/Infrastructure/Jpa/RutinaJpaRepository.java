package com.gym.backend.Rutinas.Infrastructure.Jpa;

import com.gym.backend.Rutinas.Infrastructure.Entity.RutinaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutinaJpaRepository extends JpaRepository<RutinaEntity, Long> {
    List<RutinaEntity> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    // Rutinas personales (no plantillas)
    List<RutinaEntity> findByUsuarioIdAndEsPlantillaFalseOrderByFechaCreacionDesc(Long usuarioId);

    // Plantillas públicas
    List<RutinaEntity> findByEsPlantillaTrueOrderByNombreAsc();

    Optional<RutinaEntity> findByUsuarioIdAndActivaTrue(Long usuarioId);

    @Modifying
    @Query("UPDATE RutinaEntity r SET r.activa = false WHERE r.usuarioId = :usuarioId")
    void desactivarTodasPorUsuario(@Param("usuarioId") Long usuarioId);
}
