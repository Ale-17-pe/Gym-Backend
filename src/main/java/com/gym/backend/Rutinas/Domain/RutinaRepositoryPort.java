package com.gym.backend.Rutinas.Domain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para las rutinas del usuario
 */
public interface RutinaRepositoryPort {
    Rutina guardar(Rutina rutina);

    Optional<Rutina> buscarPorId(Long id);

    List<Rutina> buscarPorUsuario(Long usuarioId);

    Optional<Rutina> buscarActivaPorUsuario(Long usuarioId);

    List<Rutina> listar();

    void eliminar(Long id);

    void desactivarTodasPorUsuario(Long usuarioId);
}
