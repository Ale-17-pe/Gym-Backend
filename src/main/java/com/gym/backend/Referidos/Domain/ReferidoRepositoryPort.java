package com.gym.backend.Referidos.Domain;

import java.util.List;
import java.util.Optional;

public interface ReferidoRepositoryPort {

    Referido guardar(Referido referido);

    Optional<Referido> buscarPorId(Long id);

    Optional<Referido> buscarPorReferidoId(Long referidoId);

    List<Referido> listarPorReferidorId(Long referidorId);

    long contarReferidosCompletados(Long referidorId);

    boolean existeReferidoPendiente(Long referidoId);
}
