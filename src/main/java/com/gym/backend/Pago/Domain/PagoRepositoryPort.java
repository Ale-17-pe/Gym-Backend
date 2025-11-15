package com.gym.backend.Pago.Domain;

import com.gym.backend.Pago.Domain.Enum.EstadoPago;

import java.util.List;
import java.util.Optional;

public interface PagoRepositoryPort {
    Pago guardar(Pago pago);
    Pago actualizar(Pago pago);
    Optional<Pago> buscarPorId(Long id);
    List<Pago> listar();
    List<Pago> listarPorUsuario(Long usuarioId);
    List<Pago> listarPorEstado(EstadoPago estado);
    List<Pago> listarPagosPendientes();
}