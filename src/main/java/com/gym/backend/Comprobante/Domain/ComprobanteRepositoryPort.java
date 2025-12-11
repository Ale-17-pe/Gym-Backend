package com.gym.backend.Comprobante.Domain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del repositorio de Comprobantes - NORMALIZADO (3NF)
 * listarPorUsuario ahora requiere JOIN con pagos (el adapter lo maneja).
 */
public interface ComprobanteRepositoryPort {
    Comprobante guardar(Comprobante comprobante);

    Optional<Comprobante> buscarPorId(Long id);

    Optional<Comprobante> buscarPorNumero(String numeroComprobante);

    Optional<Comprobante> buscarPorPagoId(Long pagoId);

    /**
     * Lista comprobantes por usuario.
     * Internamente hace JOIN con pagos para obtener el usuario_id.
     */
    List<Comprobante> listarPorUsuario(Long usuarioId);

    List<Comprobante> listar();

    boolean existePorPagoId(Long pagoId);
}
