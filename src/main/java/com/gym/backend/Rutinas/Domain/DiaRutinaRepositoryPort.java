package com.gym.backend.Rutinas.Domain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para los días de rutina
 */
public interface DiaRutinaRepositoryPort {
    DiaRutina guardar(DiaRutina diaRutina);

    Optional<DiaRutina> buscarPorId(Long id);

    List<DiaRutina> buscarPorRutina(Long rutinaId);

    void eliminar(Long id);

    void eliminarPorRutina(Long rutinaId);
}
