package com.gym.backend.Usuarios.Infrastructure.Jpa;

import com.gym.backend.Usuarios.Domain.Enum.Turno;
import com.gym.backend.Usuarios.Infrastructure.Entity.EmpleadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoJpaRepository extends JpaRepository<EmpleadoEntity, Long> {

    Optional<EmpleadoEntity> findByUsuarioId(Long usuarioId);

    Optional<EmpleadoEntity> findByPersonaId(Long personaId);

    Optional<EmpleadoEntity> findByCodigoEmpleado(String codigoEmpleado);

    List<EmpleadoEntity> findByActivo(Boolean activo);

    List<EmpleadoEntity> findByTurno(Turno turno);

    List<EmpleadoEntity> findByTipoContrato(String tipoContrato);

    // Contar empleados activos
    long countByActivoTrue();
}
