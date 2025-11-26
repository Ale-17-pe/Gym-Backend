package com.gym.backend.Comprobante.Domain;

import java.util.List;
import java.util.Optional;

public interface ComprobanteRepositoryPort {
    Comprobante guardar(Comprobante comprobante);

    Optional<Comprobante> buscarPorId(Long id);

    Optional<Comprobante> buscarPorNumero(String numeroComprobante);

    Optional<Comprobante> buscarPorPagoId(Long pagoId);

    List<Comprobante> listarPorUsuario(Long usuarioId);

    List<Comprobante> listar();

    boolean existePorPagoId(Long pagoId);
}
