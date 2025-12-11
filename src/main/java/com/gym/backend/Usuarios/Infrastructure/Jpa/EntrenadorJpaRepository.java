package com.gym.backend.Usuarios.Infrastructure.Jpa;

import com.gym.backend.Usuarios.Domain.Enum.EspecialidadEntrenador;
import com.gym.backend.Usuarios.Infrastructure.Entity.EntrenadorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntrenadorJpaRepository extends JpaRepository<EntrenadorEntity, Long> {

    Optional<EntrenadorEntity> findByUsuarioId(Long usuarioId);

    Optional<EntrenadorEntity> findByEmpleadoId(Long empleadoId);

    List<EntrenadorEntity> findByActivo(Boolean activo);

    List<EntrenadorEntity> findByEspecialidad(EspecialidadEntrenador especialidad);

    // Buscar entrenadores disponibles (con capacidad)
    @Query("SELECT e FROM EntrenadorEntity e WHERE e.activo = true AND " +
            "(e.maxClientes IS NULL OR e.maxClientes > 0)")
    List<EntrenadorEntity> findDisponibles();

    // Ordenar por rating
    List<EntrenadorEntity> findByActivoTrueOrderByRatingPromedioDesc();

    // Contar entrenadores activos
    long countByActivoTrue();
}
