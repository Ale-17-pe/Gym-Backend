package com.gym.backend.Pago.Domain;

import com.gym.backend.Pago.Domain.Enum.EstadoPago;
import com.gym.backend.Shared.Domain.BaseCrudPort;
import com.gym.backend.Shared.Domain.BaseListPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto del repositorio de Pagos.
 * Extiende interfaces segregadas para cumplir con ISP:
 * - BaseCrudPort: operaciones CRUD básicas
 * - BaseListPort: operaciones de listado
 */
public interface PagoRepositoryPort extends BaseCrudPort<Pago, Long>, BaseListPort<Pago> {

    // ==================== BÚSQUEDAS ESPECÍFICAS ====================

    Optional<Pago> buscarPorReferencia(String referencia);

    // ==================== LISTADOS POR FILTRO ====================

    List<Pago> listarPorUsuario(Long usuarioId);

    Page<Pago> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable);

    List<Pago> listarPorEstado(EstadoPago estado);

    List<Pago> listarPagosPendientes();

    List<Pago> listarPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // ==================== ESTADÍSTICAS ====================

    Double obtenerIngresosTotalesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Long contarPagosPorEstadoYFecha(EstadoPago estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Long contarPagosPorEstado(EstadoPago estado);
}