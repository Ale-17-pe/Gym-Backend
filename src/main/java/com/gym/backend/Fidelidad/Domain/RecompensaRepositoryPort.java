package com.gym.backend.Fidelidad.Domain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para Recompensa
 */
public interface RecompensaRepositoryPort {

    Recompensa guardar(Recompensa recompensa);

    Optional<Recompensa> buscarPorId(Long id);

    List<Recompensa> listarActivas();

    List<Recompensa> listarTodas();

    void eliminar(Long id);

    /**
     * Lista recompensas disponibles (activas, con stock, en fecha de vigencia)
     */
    List<Recompensa> listarDisponibles();
}
