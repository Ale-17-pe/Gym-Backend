package com.gym.backend.Usuarios.Infrastructure.Jpa;

import com.gym.backend.Usuarios.Domain.Enum.NivelExperiencia;
import com.gym.backend.Usuarios.Domain.Enum.ObjetivoFitness;
import com.gym.backend.Usuarios.Infrastructure.Entity.ClienteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {

    Optional<ClienteEntity> findByUsuarioId(Long usuarioId);

    Optional<ClienteEntity> findByPersonaId(Long personaId);

    List<ClienteEntity> findByActivo(Boolean activo);

    Page<ClienteEntity> findByActivo(Boolean activo, Pageable pageable);

    List<ClienteEntity> findByObjetivoFitness(ObjetivoFitness objetivo);

    List<ClienteEntity> findByNivelExperiencia(NivelExperiencia nivel);

    // Buscar clientes por código de referido
    List<ClienteEntity> findByCodigoReferido(String codigoReferido);

    // Contar clientes activos
    long countByActivoTrue();

    // Buscar clientes con condiciones médicas
    @Query("SELECT c FROM ClienteEntity c WHERE c.condicionesMedicas IS NOT NULL AND c.condicionesMedicas != ''")
    List<ClienteEntity> findConCondicionesMedicas();
}
