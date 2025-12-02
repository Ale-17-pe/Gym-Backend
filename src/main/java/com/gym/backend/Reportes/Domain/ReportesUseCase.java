package com.gym.backend.Reportes.Domain;

import com.gym.backend.Reportes.Domain.Record.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportesUseCase {

    private final ReportesRepositoryPort repo;

    public List<IngresosMensuales> ingresosMensuales() {
        log.info("Generando reporte de ingresos mensuales");
        return repo.ingresosMensuales();
    }

    public List<MembresiasPorEstado> membresiasPorEstado() {
        log.info("Generando reporte de membresías por estado");
        return repo.membresiasPorEstado();
    }

    public List<AsistenciasDiarias> asistenciasDiarias() {
        log.info("Generando reporte de asistencias diarias");
        return repo.asistenciasDiarias();
    }

    public List<TopPlanes> topPlanes() {
        log.info("Generando reporte de top planes");
        return repo.topPlanes();
    }

    // Nuevos reportes con parámetros
    public List<IngresosMensuales> ingresosPorRango(LocalDateTime inicio, LocalDateTime fin) {
        log.info("Generando reporte de ingresos por rango: {} - {}", inicio, fin);
        validarRangoFechas(inicio, fin);
        return repo.ingresosPorRango(inicio, fin);
    }

    public List<AsistenciasDiarias> asistenciasPorRango(LocalDateTime inicio, LocalDateTime fin) {
        log.info("Generando reporte de asistencias por rango: {} - {}", inicio, fin);
        validarRangoFechas(inicio, fin);
        return repo.asistenciasPorRango(inicio, fin);
    }

    public List<UsuariosNuevos> usuariosNuevosPorMes() {
        log.info("Generando reporte de usuarios nuevos por mes");
        return repo.usuariosNuevosPorMes();
    }

    public List<MembresiasPorPlan> membresiasPorPlan() {
        log.info("Generando reporte de membresías por plan");
        return repo.membresiasPorPlan();
    }

    public List<PagosPorMetodo> pagosPorMetodo() {
        log.info("Generando reporte de pagos por método");
        return repo.pagosPorMetodo();
    }

    public List<AsistenciasPorHora> asistenciasPorHora(LocalDate fecha) {
        log.info("Generando reporte de asistencias por hora para: {}", fecha);
        return repo.asistenciasPorHora(fecha);
    }

    public Map<String, Object> estadisticasGenerales() {
        log.info("Generando estadísticas generales del sistema");
        return repo.estadisticasGenerales();
    }

    public Map<String, Object> estadisticasPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        log.info("Generando estadísticas por fecha: {} - {}", inicio, fin);
        validarRangoFechas(inicio, fin);
        return repo.estadisticasPorFecha(inicio, fin);
    }

    public List<RendimientoMensual> rendimientoMensual() {
        log.info("Generando reporte de rendimiento mensual");
        return repo.rendimientoMensual();
    }

    public List<UsuariosActivos> usuariosMasActivos(LocalDateTime inicio, LocalDateTime fin) {
        log.info("Generando reporte de usuarios más activos: {} - {}", inicio, fin);
        validarRangoFechas(inicio, fin);
        return repo.usuariosMasActivos(inicio, fin);
    }

    public List<PlanesPopulares> planesPopulares() {
        log.info("Generando reporte de planes populares");
        return repo.planesPopulares();
    }

    public void limpiarCache() {
        log.info("Limpiando cache de reportes");
    }

    // --- NUEVOS MÉTODOS PARA REPORTE DETALLADO ---

    public List<IngresoDetallado> ingresosDetallados(com.gym.backend.Reportes.Domain.DTO.FiltroIngresoDTO filtro) {
        log.info("Generando reporte detallado de ingresos");
        validarFiltroIngresos(filtro);
        return repo.ingresosDetallados(filtro);
    }

    public com.gym.backend.Reportes.Domain.DTO.ResumenIngresoDTO resumenIngresos(
            com.gym.backend.Reportes.Domain.DTO.FiltroIngresoDTO filtro) {
        log.info("Generando resumen de ingresos");
        validarFiltroIngresos(filtro);
        return repo.resumenIngresos(filtro);
    }

    private void validarFiltroIngresos(com.gym.backend.Reportes.Domain.DTO.FiltroIngresoDTO filtro) {
        if (filtro.getFechaInicio() == null || filtro.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias");
        }
        validarRangoFechas(filtro.getFechaInicio(), filtro.getFechaFin());
    }

    private void validarRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser después de la fecha de fin");
        }

        if (inicio.plusYears(1).isBefore(fin)) {
            throw new IllegalArgumentException("El rango de fechas no puede ser mayor a 1 año");
        }
    }
}