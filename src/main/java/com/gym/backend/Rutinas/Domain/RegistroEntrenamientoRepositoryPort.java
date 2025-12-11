package com.gym.backend.Rutinas.Domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para el registro de entrenamientos (progreso)
 */
public interface RegistroEntrenamientoRepositoryPort {
    RegistroEntrenamiento guardar(RegistroEntrenamiento registro);

    Optional<RegistroEntrenamiento> buscarPorId(Long id);

    List<RegistroEntrenamiento> buscarPorUsuario(Long usuarioId);

    List<RegistroEntrenamiento> buscarPorUsuarioYRango(Long usuarioId, LocalDate inicio, LocalDate fin);

    List<RegistroEntrenamiento> buscarPorRutina(Long rutinaId);

    Optional<RegistroEntrenamiento> buscarUltimoPorUsuario(Long usuarioId);

    int contarPorUsuarioYMes(Long usuarioId, int año, int mes);

    void eliminar(Long id);
}
