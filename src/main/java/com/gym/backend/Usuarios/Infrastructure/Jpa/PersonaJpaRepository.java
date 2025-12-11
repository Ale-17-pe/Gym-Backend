package com.gym.backend.Usuarios.Infrastructure.Jpa;

import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Infrastructure.Entity.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaJpaRepository extends JpaRepository<PersonaEntity, Long> {

    Optional<PersonaEntity> findByUsuarioId(Long usuarioId);

    Optional<PersonaEntity> findByDni(String dni);

    boolean existsByDni(String dni);

    List<PersonaEntity> findByGenero(Genero genero);

    // Buscar personas con cumpleaños hoy
    @Query("SELECT p FROM PersonaEntity p WHERE " +
            "MONTH(p.fechaNacimiento) = MONTH(:fecha) AND " +
            "DAY(p.fechaNacimiento) = DAY(:fecha)")
    List<PersonaEntity> findCumpleanosHoy(@Param("fecha") LocalDate fecha);

    // Buscar por nombre o apellido
    @Query("SELECT p FROM PersonaEntity p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(p.apellido) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<PersonaEntity> buscarPorNombreOApellido(@Param("termino") String termino);
}
