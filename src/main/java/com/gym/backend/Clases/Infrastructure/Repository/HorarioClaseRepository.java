package com.gym.backend.Clases.Infrastructure.Repository;

import com.gym.backend.Clases.Infrastructure.Entity.HorarioClaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface HorarioClaseRepository extends JpaRepository<HorarioClaseEntity, Long> {

    List<HorarioClaseEntity> findByActivoTrue();

    List<HorarioClaseEntity> findByDiaSemanaAndActivoTrue(Integer diaSemana);

    List<HorarioClaseEntity> findByInstructorIdAndActivoTrue(Long instructorId);

    List<HorarioClaseEntity> findByTipoClaseIdAndActivoTrue(Long tipoClaseId);

    @Query("SELECT h FROM HorarioClaseEntity h WHERE h.diaSemana = :dia AND h.horaInicio = :hora AND h.activo = true")
    List<HorarioClaseEntity> findByDiaAndHora(@Param("dia") Integer dia, @Param("hora") LocalTime hora);
}
