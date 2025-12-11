package com.gym.backend.Pago.Domain;

import com.gym.backend.HistorialPagos.Domain.HistorialPagoUseCase;
import com.gym.backend.Pago.Domain.Enum.EstadoPago;
import com.gym.backend.Pago.Domain.Exceptions.PagoDuplicateException;
import com.gym.backend.Pago.Domain.Exceptions.PagoNotFoundException;
import com.gym.backend.Pago.Domain.Exceptions.PagoValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PagoUseCase {
    private final PagoRepositoryPort repo;
    private final HistorialPagoUseCase historialPago;

    public Pago registrar(Pago pago) {
        log.info("Registrando nuevo pago - Usuario: {}, Plan: {}, Monto: {}",
                pago.getUsuarioId(), pago.getPlanId(), pago.getMonto());

        pago.validar();

        // Validar referencia duplicada
        if (pago.getReferencia() != null && !pago.getReferencia().trim().isEmpty()) {
            repo.buscarPorReferencia(pago.getReferencia()).ifPresent(existente -> {
                throw new PagoDuplicateException(pago.getReferencia());
            });
        }

        Pago pagoParaGuardar = Pago.builder()
                .id(pago.getId())
                .usuarioId(pago.getUsuarioId())
                .planId(pago.getPlanId())
                .monto(pago.getMonto())
                .estado(EstadoPago.PENDIENTE)
                .metodoPago(pago.getMetodoPago())
                .referencia(pago.getReferencia())
                .fechaCreacion(LocalDateTime.now())
                .fechaPago(null)
                .fechaActualizacion(LocalDateTime.now())
                .build();

        Pago guardado = repo.guardar(pagoParaGuardar);

        // Registrar creación del pago en historial - NORMALIZADO 3NF
        historialPago.registrarCambioAutomatico(
                guardado.getId(),
                null, // No hay estado anterior (es la creación)
                "PENDIENTE",
                "Pago iniciado");

        return guardado;
    }

    public Pago confirmar(Long pagoId) {
        log.info("Confirmando pago ID: {}", pagoId);
        Pago pago = obtener(pagoId);

        if (!pago.puedeSerConfirmado()) {
            throw new PagoValidationException("El pago no puede ser confirmado");
        }

        String estadoAnterior = pago.getEstado().name();

        pago.confirmar();
        Pago actualizado = repo.actualizar(pago);

        historialPago.registrarCambioAutomatico(
                pago.getId(),
                estadoAnterior,
                "CONFIRMADO",
                "Confirmación manual/adaptador");
        return actualizado;
    }

    public Pago rechazar(Long pagoId) {
        log.info("Rechazando pago ID: {}", pagoId);
        Pago pago = obtener(pagoId);
        pago.rechazar();
        return repo.actualizar(pago);
    }

    public Pago cancelar(Long pagoId) {
        log.info("Cancelando pago ID: {}", pagoId);
        Pago pago = obtener(pagoId);

        if (!pago.esPendiente()) {
            throw new PagoValidationException("Solo se pueden cancelar pagos pendientes");
        }

        String estadoAnterior = pago.getEstado().name();

        pago.cancelar();
        Pago actualizado = repo.actualizar(pago);

        historialPago.registrarCambioAutomatico(
                pago.getId(),
                estadoAnterior,
                "CANCELADO",
                "Cancelación solicitada por el usuario");
        return actualizado;
    }

    public void asignarCodigo(Long pagoId, String codigo) {
        Pago pago = obtener(pagoId);
        pago.setCodigoPago(codigo);
        repo.actualizar(pago);
    }

    @Transactional(readOnly = true)
    public Pago obtener(Long id) {
        return repo.buscarPorId(id).orElseThrow(() -> new PagoNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Pago> listar() {
        return repo.listar();
    }

    @Transactional(readOnly = true)
    public Page<Pago> listarPaginated(Pageable pageable) {
        return repo.listarPaginated(pageable);
    }

    @Transactional(readOnly = true)
    public List<Pago> listarPorUsuario(Long usuarioId) {
        return repo.listarPorUsuario(usuarioId);
    }

    @Transactional(readOnly = true)
    public Page<Pago> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable) {
        return repo.listarPorUsuarioPaginated(usuarioId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Pago> listarPorEstado(EstadoPago estado) {
        return repo.listarPorEstado(estado);
    }

    @Transactional(readOnly = true)
    public List<Pago> listarPendientes() {
        return repo.listarPagosPendientes();
    }

    // Métodos para reporting
    @Transactional(readOnly = true)
    public List<Pago> listarPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return repo.listarPorFecha(fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public Double obtenerIngresosTotalesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return repo.obtenerIngresosTotalesPorFecha(fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public Long contarPagosPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return repo.contarPagosPorEstadoYFecha(EstadoPago.CONFIRMADO, fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public EstadisticasMensual obtenerEstadisticasMensual(int año, int mes) {
        LocalDateTime inicio = LocalDateTime.of(año, mes, 1, 0, 0);
        LocalDateTime fin = inicio.plusMonths(1).minusSeconds(1);

        Double ingresos = repo.obtenerIngresosTotalesPorFecha(inicio, fin);
        Long confirmados = repo.contarPagosPorEstadoYFecha(EstadoPago.CONFIRMADO, inicio, fin);
        Long pendientes = repo.contarPagosPorEstadoYFecha(EstadoPago.PENDIENTE, inicio, fin);
        Long rechazados = repo.contarPagosPorEstadoYFecha(EstadoPago.RECHAZADO, inicio, fin);

        return new EstadisticasMensual(año, mes, ingresos, confirmados, pendientes, rechazados);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticasDiarias() {
        LocalDateTime hoy = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime finHoy = hoy.plusDays(1).minusSeconds(1);
        Double ingresosHoy = repo.obtenerIngresosTotalesPorFecha(hoy, finHoy);
        Long pagosPendientes = repo.contarPagosPorEstado(EstadoPago.PENDIENTE);
        Long pagosConfirmadosHoy = repo.contarPagosPorEstadoYFecha(EstadoPago.CONFIRMADO, hoy, finHoy);
        return Map.of(
                "ingresosHoy", ingresosHoy != null ? ingresosHoy : 0.0,
                "pagosPendientes", pagosPendientes != null ? pagosPendientes : 0L,
                "pagosConfirmadosHoy", pagosConfirmadosHoy != null ? pagosConfirmadosHoy : 0L,
                "fechaConsulta", LocalDateTime.now());
    }

    public record EstadisticasMensual(int año, int mes, Double ingresosTotales,
            Long pagosConfirmados, Long pagosPendientes,
            Long pagosRechazados) {
    }
}
