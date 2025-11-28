package com.gym.backend.Clases.Infrastructure.Repository;

import com.gym.backend.Clases.Domain.Enum.EstadoSesion;
import com.gym.backend.Clases.Infrastructure.Entity.SesionClaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SesionClaseRepository extends JpaRepository<SesionClaseEntity, Long> {

    List<SesionClaseEntity> findByFecha(LocalDate fecha);

    List<SesionClaseEntity> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    Optional<SesionClaseEntity> findByHorarioClaseIdAndFecha(Long horarioClaseId, LocalDate fecha);

    List<SesionClaseEntity> findByEstado(EstadoSesion estado);

    @Query("SELECT s FROM SesionClaseEntity s WHERE s.fecha BETWEEN :inicio AND :fin AND s.estado = 'PROGRAMADA' ORDER BY s.fecha, s.horarioClase.horaInicio")
    List<SesionClaseEntity> findSesionesProgramadas(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    boolean existsByHorarioClaseIdAndFecha(Long horarioClaseId, LocalDate fecha);
}
