package com.gym.backend.HistorialMembresias.Domain;

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
public class HistorialMembresiaUseCase {

    private final HistorialMembresiaRepositoryPort repo;

    public HistorialMembresia registrarCambio(HistorialMembresia historial) {
        log.info("Registrando cambio en historial de membresía - MembresiaID: {}, Acción: {}",
                historial.getMembresiaId(), historial.getAccion());

        if (historial.getMembresiaId() == null) {
            throw new IllegalArgumentException("El ID de membresía es requerido");
        }
        if (historial.getAccion() == null) {
            throw new IllegalArgumentException("La acción es requerida");
        }

        HistorialMembresia historialCompleto = HistorialMembresia.builder()
                .id(historial.getId())
                .membresiaId(historial.getMembresiaId())
                .usuarioId(historial.getUsuarioId())
                .planId(historial.getPlanId())
                .accion(historial.getAccion())
                .estadoAnterior(historial.getEstadoAnterior())
                .estadoNuevo(historial.getEstadoNuevo())
                .motivoCambio(historial.getMotivoCambio())
                .usuarioModificacion(historial.getUsuarioModificacion())
                .ipOrigen(historial.getIpOrigen())
                .fechaCambio(LocalDateTime.now())
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        HistorialMembresia guardado = repo.registrar(historialCompleto);
        log.info("Cambio registrado exitosamente - HistorialID: {}", guardado.getId());
        return guardado;
    }

    public HistorialMembresia registrarCambioAutomatico(Long membresiaId, Long usuarioId, Long planId,
                                                        String accion, String estadoAnterior, String estadoNuevo,
                                                        String motivo) {
        HistorialMembresia historial = HistorialMembresia.builder()
                .membresiaId(membresiaId)
                .usuarioId(usuarioId)
                .planId(planId)
                .accion(accion)
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
    public List<HistorialMembresia> listar() {
        return repo.listar();
    }

    @Transactional(readOnly = true)
    public Page<HistorialMembresia> listarPaginated(Pageable pageable) {
        return repo.listarPaginated(pageable);
    }

    @Transactional(readOnly = true)
    public List<HistorialMembresia> listarPorUsuario(Long usuarioId) {
        return repo.listarPorUsuario(usuarioId);
    }

    @Transactional(readOnly = true)
    public Page<HistorialMembresia> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable) {
        return repo.listarPorUsuarioPaginated(usuarioId, pageable);
    }

    @Transactional(readOnly = true)
    public List<HistorialMembresia> listarPorMembresia(Long membresiaId) {
        return repo.listarPorMembresia(membresiaId);
    }

    @Transactional(readOnly = true)
    public List<HistorialMembresia> listarPorAccion(String accion) {
        return repo.listarPorAccion(accion);
    }

    @Transactional(readOnly = true)
    public List<HistorialMembresia> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return repo.listarPorRangoFechas(inicio, fin);
    }

    @Transactional(readOnly = true)
    public HistorialMembresia obtenerUltimoCambio(Long membresiaId) {
        return repo.obtenerUltimoCambio(membresiaId);
    }

    @Transactional(readOnly = true)
    public List<HistorialMembresia> obtenerCambiosRecientes(int limite) {
        return repo.obtenerCambiosRecientes(limite);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticas() {
        Long totalRegistros = repo.contarTotal();
        Long cambiosHoy = repo.contarCambiosHoy();
        Long creaciones = repo.contarPorAccion("CREAR");
        Long extensiones = repo.contarPorAccion("EXTENDER");
        Long suspensiones = repo.contarPorAccion("SUSPENDER");

        return Map.of(
                "totalRegistros", totalRegistros,
                "cambiosHoy", cambiosHoy,
                "creaciones", creaciones,
                "extensiones", extensiones,
                "suspensiones", suspensiones,
                "fechaConsulta", LocalDateTime.now()
        );
    }

    public Map<String, Long> obtenerEstadisticasPorMes(int año, int mes) {
        Long totalCambios = repo.contarCambiosPorMes(año, mes);
        Long creaciones = repo.contarCreacionesPorMes(año, mes);
        Long extensiones = repo.contarExtensionesPorMes(año, mes);

        Map<String, Long> map = new HashMap<>();
        map.put("totalCambios", totalCambios);
        map.put("creaciones", creaciones);
        map.put("extensiones", extensiones);
        map.put("año", (long) año);
        map.put("mes", (long) mes);

        return map;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerTimelineMembresia(Long membresiaId) {
        return repo.listarPorMembresia(membresiaId).stream()
                .map(hm -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("fecha", hm.getFechaCambio());
                    map.put("accion", hm.getAccion());
                    map.put("estadoAnterior", hm.getEstadoAnterior());
                    map.put("estadoNuevo", hm.getEstadoNuevo());
                    map.put("motivo", hm.getMotivoCambio());
                    map.put("usuario", hm.getUsuarioModificacion());
                    map.put("tipoCambio", hm.obtenerTipoCambio());
                    return map;
                })
                .toList();
    }
}