package com.gym.backend.HistorialPagos.Domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HistorialPagoUseCase {

    private final HistorialPagoRepositoryPort repo;

    public HistorialPago registrarCambio(HistorialPago historial) {
        log.info("Registrando cambio en historial de pago - PagoID: {}, Estado: {} -> {}",
                historial.getPagoId(), historial.getEstadoAnterior(), historial.getEstadoNuevo());

        // Validar datos requeridos
        if (historial.getPagoId() == null) {
            throw new IllegalArgumentException("El ID de pago es requerido");
        }
        if (historial.getEstadoNuevo() == null) {
            throw new IllegalArgumentException("El estado nuevo es requerido");
        }

        HistorialPago historialCompleto = HistorialPago.builder()
                .id(historial.getId())
                .pagoId(historial.getPagoId())
                .usuarioId(historial.getUsuarioId())
                .planId(historial.getPlanId())
                .monto(historial.getMonto())
                .estadoAnterior(historial.getEstadoAnterior())
                .estadoNuevo(historial.getEstadoNuevo())
                .motivoCambio(historial.getMotivoCambio())
                .usuarioModificacion(historial.getUsuarioModificacion())
                .ipOrigen(historial.getIpOrigen())
                .fechaCambio(LocalDateTime.now())
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        HistorialPago guardado = repo.registrar(historialCompleto);
        log.info("Cambio registrado exitosamente - HistorialID: {}", guardado.getId());
        return guardado;
    }

    public HistorialPago registrarCambioAutomatico(Long pagoId, Long usuarioId, Long planId, Double monto,
            String estadoAnterior, String estadoNuevo, String motivo) {
        HistorialPago historial = HistorialPago.builder()
                .pagoId(pagoId)
                .usuarioId(usuarioId)
                .planId(planId)
                .monto(monto)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .motivoCambio(motivo)
                .usuarioModificacion("SISTEMA")
                .ipOrigen("INTERNO")
                .fechaCambio(LocalDateTime.now())
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        return registrarCambio(historial);
    }

    @Transactional(readOnly = true)
    public List<HistorialPago> listar() {
        return repo.listar();
    }

    @Transactional(readOnly = true)
    public Page<HistorialPago> listarPaginated(Pageable pageable) {
        return repo.listarPaginated(pageable);
    }

    @Transactional(readOnly = true)
    public List<HistorialPago> listarPorUsuario(Long usuarioId) {
        return repo.listarPorUsuario(usuarioId);
    }

    @Transactional(readOnly = true)
    public Page<HistorialPago> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable) {
        return repo.listarPorUsuarioPaginated(usuarioId, pageable);
    }

    @Transactional(readOnly = true)
    public List<HistorialPago> listarPorPago(Long pagoId) {
        return repo.listarPorPago(pagoId);
    }

    @Transactional(readOnly = true)
    public List<HistorialPago> listarPorEstado(String estado) {
        return repo.listarPorEstado(estado);
    }

    @Transactional(readOnly = true)
    public List<HistorialPago> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return repo.listarPorRangoFechas(inicio, fin);
    }

    @Transactional(readOnly = true)
    public HistorialPago obtenerUltimoCambio(Long pagoId) {
        return repo.obtenerUltimoCambio(pagoId);
    }

    @Transactional(readOnly = true)
    public List<HistorialPago> obtenerCambiosRecientes(int limite) {
        return repo.obtenerCambiosRecientes(limite);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticas() {
        Long totalRegistros = repo.contarTotal();
        Long cambiosHoy = repo.contarCambiosHoy();
        Long confirmaciones = repo.contarPorEstadoNuevo("CONFIRMADO");
        Long rechazos = repo.contarPorEstadoNuevo("RECHAZADO");
        Long cancelaciones = repo.contarPorEstadoNuevo("CANCELADO");

        return Map.of(
                "totalRegistros", totalRegistros,
                "cambiosHoy", cambiosHoy,
                "confirmaciones", confirmaciones,
                "rechazos", rechazos,
                "cancelaciones", cancelaciones,
                "fechaConsulta", LocalDateTime.now());
    }

    public Map<String, Long> obtenerEstadisticasPorMes(int año, int mes) {
        Long totalCambios = repo.contarCambiosPorMes(año, mes);
        Long confirmaciones = repo.contarConfirmacionesPorMes(año, mes);
        Long rechazos = repo.contarRechazosPorMes(año, mes);

        Map<String, Long> map = new HashMap<>();
        map.put("totalCambios", totalCambios);
        map.put("confirmaciones", confirmaciones);
        map.put("rechazos", rechazos);
        map.put("año", (long) año); // 🔥 convertir int → Long
        map.put("mes", (long) mes); // 🔥 convertir int → Long

        return map;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerTimelinePago(Long pagoId) {
        return repo.listarPorPago(pagoId).stream()
                .map(hp -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("fecha", hp.getFechaCambio());
                    map.put("estadoAnterior", hp.getEstadoAnterior());
                    map.put("estadoNuevo", hp.getEstadoNuevo());
                    map.put("motivo", hp.getMotivoCambio());
                    map.put("usuario", hp.getUsuarioModificacion());
                    map.put("tipoCambio", hp.obtenerTipoCambio());
                    return map;
                })
                .toList();
    }

}