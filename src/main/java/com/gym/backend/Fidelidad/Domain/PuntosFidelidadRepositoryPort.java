package com.gym.backend.Fidelidad.Domain;

import java.util.Optional;

/**
 * Puerto de repositorio para PuntosFidelidad
 */
public interface PuntosFidelidadRepositoryPort {

    PuntosFidelidad guardar(PuntosFidelidad puntos);

    Optional<PuntosFidelidad> buscarPorUsuarioId(Long usuarioId);

    boolean existePorUsuarioId(Long usuarioId);
}
