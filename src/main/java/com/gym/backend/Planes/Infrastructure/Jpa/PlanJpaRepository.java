package com.gym.backend.Planes.Infrastructure.Jpa;

import com.gym.backend.Planes.Infrastructure.Entity.PlanEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface PlanJpaRepository extends JpaRepository<PlanEntity, Long> {
    Optional<PlanEntity> findByNombrePlan(String nombrePlan);
    List<PlanEntity> findByActivoTrue();
    Page<PlanEntity> findByActivoTrue(Pageable pageable);
    List<PlanEntity> findByActivoFalse();
    List<PlanEntity> findByCategoria(String categoria);
    List<PlanEntity> findByDestacadoTrueAndActivoTrue();
    List<PlanEntity> findByPrecioLessThanEqual(Double precioMax);

    @Query("SELECT p FROM PlanEntity p WHERE p.precio BETWEEN :precioMin AND :precioMax AND p.activo = true")
    Page<PlanEntity> findByPrecioBetweenAndActivoTrue(@Param("precioMin") Double precioMin,
                                                      @Param("precioMax") Double precioMax,
                                                      Pageable pageable);
    @Query("SELECT p FROM PlanEntity p WHERE p.duracionDias = :duracion AND p.activo = true")
    List<PlanEntity> findByDuracionDiasAndActivoTrue(@Param("duracion") Integer duracion);

    // Métodos para analytics
    @Query("SELECT COUNT(p) FROM PlanEntity p WHERE p.activo = true")
    Long countByActivoTrue();

    @Query("SELECT AVG(p.precio) FROM PlanEntity p WHERE p.activo = true")
    Double findPrecioPromedioByActivoTrue();

    @Query("SELECT p FROM PlanEntity p ORDER BY p.vecesContratado DESC LIMIT 1")
    Optional<PlanEntity> findTopByOrderByVecesContratadoDesc();

    @Query("SELECT p FROM PlanEntity p WHERE p.vecesContratado > 0 ORDER BY p.ratingPromedio DESC LIMIT 5")
    List<PlanEntity> findTop5ByOrderByRatingPromedioDesc();
}
