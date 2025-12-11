package com.gym.backend.Rutinas.Infrastructure.Jpa;

import com.gym.backend.Rutinas.Domain.Enum.GrupoMuscular;
import com.gym.backend.Rutinas.Infrastructure.Entity.EjercicioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjercicioJpaRepository extends JpaRepository<EjercicioEntity, Long> {
    List<EjercicioEntity> findByGrupoMuscular(GrupoMuscular grupoMuscular);

    List<EjercicioEntity> findByNombreContainingIgnoreCase(String nombre);

    List<EjercicioEntity> findByActivoTrue();

    List<EjercicioEntity> findByActivoTrueOrderByGrupoMuscularAscNombreAsc();
}
