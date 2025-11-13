package com.gym.backend.Pago.Domain;

import java.util.List;

public interface PagoRepositoryPort {

    Pago registrar(Pago pago);

    Pago actualizar(Long id, Pago pago);

    Pago obtenerPorId(Long id);

    List<Pago> listar();

    List<Pago> listarPorUsuario(Long usuarioId);

    List<Pago> listarPorMembresia(Long membresiaId);
}