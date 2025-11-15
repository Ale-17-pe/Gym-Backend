package com.gym.backend.Planes.Infrastructure.Jpa;

import com.gym.backend.Planes.Infrastructure.Entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanJpaRepository extends JpaRepository<PlanEntity, Long> {
    Optional<PlanEntity> findByNombrePlan(String nombrePlan);
    List<PlanEntity> findByActivoTrue();
    List<PlanEntity> findByActivoFalse();

    @Query("SELECT p FROM PlanEntity p WHERE p.precio BETWEEN :precioMin AND :precioMax")
    List<PlanEntity> findByPrecioBetween(@Param("precioMin") Double precioMin,
                                         @Param("precioMax") Double precioMax);

    @Query("SELECT p FROM PlanEntity p WHERE p.duracionDias = :duracion")
    List<PlanEntity> findByDuracionDias(@Param("duracion") Integer duracion);
}
