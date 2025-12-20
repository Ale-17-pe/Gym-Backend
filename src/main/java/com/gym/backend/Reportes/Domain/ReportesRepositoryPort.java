package com.gym.backend.Reportes.Domain;

import com.gym.backend.Reportes.Domain.Record.*;
import com.gym.backend.Reportes.Domain.Record.IngresoDetallado;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReportesRepositoryPort {

    // Reportes básicos existentes
    List<IngresosMensuales> ingresosMensuales();

    List<MembresiasPorEstado> membresiasPorEstado();

    List<AsistenciasDiarias> asistenciasDiarias();

    List<TopPlanes> topPlanes();

    // Nuevos reportes
    List<IngresosMensuales> ingresosPorRango(LocalDateTime inicio, LocalDateTime fin);

    List<AsistenciasDiarias> asistenciasPorRango(LocalDateTime inicio, LocalDateTime fin);

    List<UsuariosNuevos> usuariosNuevosPorMes();

    List<MembresiasPorPlan> membresiasPorPlan();

    List<PagosPorMetodo> pagosPorMetodo();

    List<AsistenciasPorHora> asistenciasPorHora(LocalDate fecha);

    List<RendimientoMensual> rendimientoMensual();

    List<UsuariosActivos> usuariosMasActivos(LocalDateTime inicio, LocalDateTime fin);

    List<PlanesPopulares> planesPopulares();

    // Estadísticas
    Map<String, Object> estadisticasGenerales();

    Map<String, Object> estadisticasPorFecha(LocalDateTime inicio, LocalDateTime fin);

    // Reporte detallado de ingresos
    List<IngresoDetallado> ingresosDetallados(com.gym.backend.Reportes.Domain.DTO.FiltroIngresoDTO filtro);

    com.gym.backend.Reportes.Domain.DTO.ResumenIngresoDTO resumenIngresos(
            com.gym.backend.Reportes.Domain.DTO.FiltroIngresoDTO filtro);

    // ========== NUEVOS MÉTODOS ==========
    Map<String, Object> comparativaMensual();

    List<Map<String, Object>> asistenciasSemanal();

    Map<String, Object> renovacionesProximas(int dias);

    // ========== MÉTODOS PARA EXPORTACIÓN ==========
    List<com.gym.backend.Reportes.Application.DTO.IngresoDetalladoDTO> obtenerIngresosDetallados(
            com.gym.backend.Reportes.Application.DTO.FiltroIngresoDTO filtro);

    com.gym.backend.Reportes.Application.DTO.ResumenIngresoDTO obtenerResumenIngresos(
            com.gym.backend.Reportes.Application.DTO.FiltroIngresoDTO filtro);
}