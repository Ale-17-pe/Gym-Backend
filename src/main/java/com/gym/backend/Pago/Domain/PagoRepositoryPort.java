package com.gym.backend.Pago.Domain;

import com.gym.backend.Pago.Domain.Enum.EstadoPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PagoRepositoryPort {
    Pago guardar(Pago pago);

    Pago actualizar(Pago pago);

    Optional<Pago> buscarPorId(Long id);

    Optional<Pago> buscarPorReferencia(String referencia);

    List<Pago> listar();

    Page<Pago> listarPaginated(Pageable pageable);

    List<Pago> listarPorUsuario(Long usuarioId);

    Page<Pago> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable);

    List<Pago> listarPorEstado(EstadoPago estado);

    List<Pago> listarPagosPendientes();

    List<Pago> listarPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Double obtenerIngresosTotalesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Long contarPagosPorEstadoYFecha(EstadoPago estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Long contarPagosPorEstado(EstadoPago estado);

}