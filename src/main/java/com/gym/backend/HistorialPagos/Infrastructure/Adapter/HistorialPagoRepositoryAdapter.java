package com.gym.backend.HistorialPagos.Infrastructure.Adapter;

import com.gym.backend.HistorialPagos.Domain.HistorialPago;
import com.gym.backend.HistorialPagos.Domain.HistorialPagoRepositoryPort;
import com.gym.backend.HistorialPagos.Infrastructure.Entity.HistorialPagoEntity;
import com.gym.backend.HistorialPagos.Infrastructure.Jpa.HistorialPagoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Adapter para repositorio de Historial de Pagos - NORMALIZADO (3NF)
 */
@Component
@RequiredArgsConstructor
public class HistorialPagoRepositoryAdapter implements HistorialPagoRepositoryPort {

    private final HistorialPagoJpaRepository jpa;

    @Override
    public HistorialPago registrar(HistorialPago historial) {
        return toDomain(jpa.save(toEntity(historial)));
    }

    @Override
    public List<HistorialPago> listar() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Page<HistorialPago> listarPaginated(Pageable pageable) {
        return jpa.findAll(pageable).map(this::toDomain);
    }

    @Override
    public List<HistorialPago> listarPorPago(Long pagoId) {
        return jpa.findByPagoId(pagoId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<HistorialPago> listarPorEstado(String estado) {
        return jpa.findByEstadoNuevo(estado).stream().map(this::toDomain).toList();
    }

    @Override
    public List<HistorialPago> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return jpa.findByFechaCambioBetween(inicio, fin).stream().map(this::toDomain).toList();
    }

    @Override
    public HistorialPago obtenerUltimoCambio(Long pagoId) {
        return jpa.findTopByPagoIdOrderByFechaCambioDesc(pagoId)
                .map(this::toDomain)
                .orElse(null);
    }

    @Override
    public List<HistorialPago> obtenerCambiosRecientes(int limite) {
        return jpa.findTopN(PageRequest.of(0, limite))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Long contarTotal() {
        return jpa.count();
    }

    @Override
    public Long contarCambiosHoy() {
        LocalDateTime inicio = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime fin = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        return jpa.countByFechaCambioBetween(inicio, fin);
    }

    @Override
    public Long contarPorEstadoNuevo(String estado) {
        return jpa.countByEstadoNuevo(estado);
    }

    @Override
    public Long contarCambiosPorMes(int año, int mes) {
        return jpa.countByYearAndMonth(año, mes);
    }

    @Override
    public Long contarConfirmacionesPorMes(int año, int mes) {
        return jpa.countByEstadoNuevoAndYearAndMonth("CONFIRMADO", año, mes);
    }

    @Override
    public Long contarRechazosPorMes(int año, int mes) {
        return jpa.countByEstadoNuevoAndYearAndMonth("RECHAZADO", año, mes);
    }

    private HistorialPago toDomain(HistorialPagoEntity entity) {
        return HistorialPago.builder()
                .id(entity.getId())
                .pagoId(entity.getPagoId())
                // ELIMINADOS 3NF: usuarioId, planId, monto
                .estadoAnterior(entity.getEstadoAnterior())
                .estadoNuevo(entity.getEstadoNuevo())
                .motivoCambio(entity.getMotivoCambio())
                .usuarioModificacion(entity.getUsuarioModificacion())
                .ipOrigen(entity.getIpOrigen())
                .fechaCambio(entity.getFechaCambio())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private HistorialPagoEntity toEntity(HistorialPago domain) {
        return HistorialPagoEntity.builder()
                .id(domain.getId())
                .pagoId(domain.getPagoId())
                // ELIMINADOS 3NF: usuarioId, planId, monto
                .estadoAnterior(domain.getEstadoAnterior())
                .estadoNuevo(domain.getEstadoNuevo())
                .motivoCambio(domain.getMotivoCambio())
                .usuarioModificacion(domain.getUsuarioModificacion())
                .ipOrigen(domain.getIpOrigen())
                .fechaCambio(domain.getFechaCambio())
                .fechaCreacion(domain.getFechaCreacion())
                .fechaActualizacion(domain.getFechaActualizacion())
                .build();
    }
}