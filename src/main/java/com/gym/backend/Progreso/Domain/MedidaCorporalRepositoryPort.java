package com.gym.backend.Progreso.Domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para medidas corporales
 */
public interface MedidaCorporalRepositoryPort {
    MedidaCorporal guardar(MedidaCorporal medida);

    Optional<MedidaCorporal> buscarPorId(Long id);

    List<MedidaCorporal> buscarPorUsuario(Long usuarioId);

    List<MedidaCorporal> buscarPorUsuarioYRango(Long usuarioId, LocalDate inicio, LocalDate fin);

    Optional<MedidaCorporal> buscarUltimaPorUsuario(Long usuarioId);

    Optional<MedidaCorporal> buscarPorUsuarioYFecha(Long usuarioId, LocalDate fecha);

    void eliminar(Long id);
}
