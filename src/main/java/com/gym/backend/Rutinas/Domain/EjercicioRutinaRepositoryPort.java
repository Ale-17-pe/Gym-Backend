package com.gym.backend.Rutinas.Domain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para los ejercicios asignados a días de rutina
 */
public interface EjercicioRutinaRepositoryPort {
    EjercicioRutina guardar(EjercicioRutina ejercicioRutina);

    Optional<EjercicioRutina> buscarPorId(Long id);

    List<EjercicioRutina> buscarPorDiaRutina(Long diaRutinaId);

    void eliminar(Long id);

    void eliminarPorDiaRutina(Long diaRutinaId);
}
