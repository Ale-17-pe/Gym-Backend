package com.gym.backend.Fidelidad.Domain;

import com.gym.backend.Fidelidad.Domain.Enum.EstadoCanje;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para Canje
 */
public interface CanjeRepositoryPort {

    Canje guardar(Canje canje);

    Optional<Canje> buscarPorId(Long id);

    Optional<Canje> buscarPorCodigo(String codigoCanje);

    List<Canje> listarPorUsuario(Long usuarioId);

    List<Canje> listarPorEstado(EstadoCanje estado);

    /**
     * Busca canjes pendientes de tipo descuento para un usuario
     */
    List<Canje> listarDescuentosPendientesPorUsuario(Long usuarioId);

    /**
     * Cuenta canjes realizados por un usuario
     */
    long contarPorUsuario(Long usuarioId);
}
