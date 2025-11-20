package com.gym.backend.Reportes.Infrastructure.Adapter;

import com.gym.backend.Reportes.Domain.*;
import com.gym.backend.Reportes.Domain.Record.*;
import com.gym.backend.Reportes.Infrastructure.Jpa.ReportesJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportesRepositoryAdapter implements ReportesRepositoryPort {

    private final ReportesJpaRepository jpa;

    @Override
    public List<IngresosMensuales> ingresosMensuales() {
        return jpa.ingresosMensuales().stream()
                .map(r -> new IngresosMensuales((String) r[0], ((Number) r[1]).doubleValue()))
                .toList();
    }

    @Override
    public List<MembresiasPorEstado> membresiasPorEstado() {
        return jpa.membresiasPorEstado().stream()
                .map(r -> new MembresiasPorEstado((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }

    @Override
    public List<AsistenciasDiarias> asistenciasDiarias() {
        return jpa.asistenciasDiarias().stream()
                .map(r -> new AsistenciasDiarias((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }

    @Override
    public List<TopPlanes> topPlanes() {
        return jpa.topPlanes().stream()
                .map(r -> new TopPlanes((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }

    @Override
    public List<IngresosMensuales> ingresosPorRango(LocalDateTime inicio, LocalDateTime fin) {
        return jpa.ingresosPorRango(inicio, fin).stream()
                .map(r -> new IngresosMensuales((String) r[0], ((Number) r[1]).doubleValue()))
                .toList();
    }

    @Override
    public List<AsistenciasDiarias> asistenciasPorRango(LocalDateTime inicio, LocalDateTime fin) {
        return jpa.asistenciasPorRango(inicio, fin).stream()
                .map(r -> new AsistenciasDiarias((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }

    @Override
    public List<UsuariosNuevos> usuariosNuevosPorMes() {
        return jpa.usuariosNuevosPorMes().stream()
                .map(r -> new UsuariosNuevos((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }

    @Override
    public List<MembresiasPorPlan> membresiasPorPlan() {
        return jpa.membresiasPorPlan().stream()
                .map(r -> new MembresiasPorPlan((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }

    @Override
    public List<PagosPorMetodo> pagosPorMetodo() {
        return jpa.pagosPorMetodo().stream()
                .map(r -> new PagosPorMetodo(
                        (String) r[0],
                        ((Number) r[1]).longValue(),
                        ((Number) r[2]).doubleValue()
                ))
                .toList();
    }

    @Override
    public List<AsistenciasPorHora> asistenciasPorHora(LocalDate fecha) {
        return jpa.asistenciasPorHora(fecha).stream()
                .map(r -> new AsistenciasPorHora((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }

    @Override
    public List<RendimientoMensual> rendimientoMensual() {
        return jpa.rendimientoMensual().stream()
                .map(r -> new RendimientoMensual(
                        (String) r[0],
                        ((Number) r[1]).doubleValue(),
                        ((Number) r[2]).longValue(),
                        ((Number) r[3]).longValue()
                ))
                .toList();
    }

    @Override
    public List<UsuariosActivos> usuariosMasActivos(LocalDateTime inicio, LocalDateTime fin) {
        return jpa.usuariosMasActivos(inicio, fin).stream()
                .map(r -> new UsuariosActivos(
                        (String) r[0],
                        ((Number) r[1]).longValue(),
                        (String) r[2]
                ))
                .toList();
    }

    @Override
    public List<PlanesPopulares> planesPopulares() {
        return jpa.planesPopulares().stream()
                .map(r -> new PlanesPopulares(
                        (String) r[0],
                        ((Number) r[1]).doubleValue(),
                        ((Number) r[2]).longValue(),
                        ((Number) r[3]).doubleValue()
                ))
                .toList();
    }

    @Override
    public Map<String, Object> estadisticasGenerales() {
        return jpa.estadisticasGenerales();
    }

    @Override
    public Map<String, Object> estadisticasPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        return jpa.estadisticasPorFecha(inicio, fin);
    }
}