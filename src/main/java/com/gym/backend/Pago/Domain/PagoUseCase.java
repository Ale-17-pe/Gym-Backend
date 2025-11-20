package com.gym.backend.Pago.Domain;

import com.gym.backend.HistorialPagos.Domain.HistorialPagoUseCase;
import com.gym.backend.Pago.Domain.Enum.EstadoPago;
import com.gym.backend.Pago.Domain.Exceptions.PagoDuplicateException;
import com.gym.backend.Pago.Domain.Exceptions.PagoNotFoundException;
import com.gym.backend.Pago.Domain.Exceptions.PagoValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

        return repo.guardar(pagoParaGuardar);
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
                pago.getUsuarioId(),
                pago.getPlanId(),
                pago.getMonto(),
                estadoAnterior,
                "CONFIRMADO",
                "Confirmación manual/adaptador"
        );
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

        pago.cancelar();
        return repo.actualizar(pago);
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

    public record EstadisticasMensual(int año, int mes, Double ingresosTotales,
                                      Long pagosConfirmados, Long pagosPendientes,
                                      Long pagosRechazados) {}
}