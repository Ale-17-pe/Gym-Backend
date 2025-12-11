package com.gym.backend.Usuarios.Domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Puerto del repositorio de Persona (datos personales)
 */
public interface PersonaRepositoryPort {

    Persona guardar(Persona persona);

    Optional<Persona> buscarPorId(Long id);

    Optional<Persona> buscarPorUsuarioId(Long usuarioId);

    Optional<Persona> buscarPorDni(String dni);

    boolean existePorDni(String dni);

    List<Persona> buscarCumpleanosHoy(LocalDate fecha);

    List<Persona> buscarPorNombreOApellido(String termino);

    void eliminar(Long id);
}
