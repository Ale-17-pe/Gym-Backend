package com.gym.backend.Progreso.Domain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para objetivos físicos
 */
public interface ObjetivoFisicoRepositoryPort {
    ObjetivoFisico guardar(ObjetivoFisico objetivo);

    Optional<ObjetivoFisico> buscarPorId(Long id);

    List<ObjetivoFisico> buscarPorUsuario(Long usuarioId);

    Optional<ObjetivoFisico> buscarActivoPorUsuario(Long usuarioId);

    List<ObjetivoFisico> buscarCompletadosPorUsuario(Long usuarioId);

    void eliminar(Long id);
}
