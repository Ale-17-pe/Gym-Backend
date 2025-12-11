package com.gym.backend.Rutinas.Domain;

import com.gym.backend.Rutinas.Domain.Enum.GrupoMuscular;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para el catálogo de ejercicios
 */
public interface EjercicioRepositoryPort {
    Ejercicio guardar(Ejercicio ejercicio);

    Optional<Ejercicio> buscarPorId(Long id);

    List<Ejercicio> buscarPorGrupoMuscular(GrupoMuscular grupoMuscular);

    List<Ejercicio> buscarPorNombre(String nombre);

    List<Ejercicio> listarActivos();

    List<Ejercicio> listar();

    void eliminar(Long id);
}
