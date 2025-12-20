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
                                .map(r -> new TopPlanes(
                                                (String) r[0],
                                                ((Number) r[1]).intValue(),
                                                ((Number) r[2]).doubleValue()))
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
                                .map(r -> new UsuariosNuevos(
                                                ((Number) r[0]).intValue(),
                                                ((Number) r[1]).intValue(),
                                                ((Number) r[2]).intValue()))
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
                                                ((Number) r[1]).intValue(),
                                                ((Number) r[2]).doubleValue()))
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
                                                ((Number) r[3]).longValue()))
                                .toList();
        }

        @Override
        public List<UsuariosActivos> usuariosMasActivos(LocalDateTime inicio, LocalDateTime fin) {
                return jpa.usuariosMasActivos(inicio, fin).stream()
                                .map(r -> new UsuariosActivos(
                                                (String) r[0],
                                                ((Number) r[1]).longValue(),
                                                (String) r[2]))
                                .toList();
        }

        @Override
        public List<PlanesPopulares> planesPopulares() {
                return jpa.planesPopulares().stream()
                                .map(r -> new PlanesPopulares(
                                                (String) r[0],
                                                ((Number) r[1]).doubleValue(),
                                                ((Number) r[2]).longValue(),
                                                ((Number) r[3]).doubleValue()))
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

        @Override
        public List<IngresoDetallado> ingresosDetallados(com.gym.backend.Reportes.Domain.DTO.FiltroIngresoDTO filtro) {
                return jpa.ingresosDetallados(filtro).stream()
                                .map(r -> new IngresoDetallado(
                                                ((java.sql.Timestamp) r[0]).toLocalDateTime(),
                                                (String) r[1],
                                                (String) r[2],
                                                (String) r[3],
                                                ((Number) r[4]).doubleValue(),
                                                (String) r[5],
                                                (String) r[6],
                                                (String) r[7],
                                                (String) r[8]))
                                .toList();
        }

        @Override
        public com.gym.backend.Reportes.Domain.DTO.ResumenIngresoDTO resumenIngresos(
                        com.gym.backend.Reportes.Domain.DTO.FiltroIngresoDTO filtro) {
                Map<String, Object> result = jpa.resumenIngresos(filtro);
                return com.gym.backend.Reportes.Domain.DTO.ResumenIngresoDTO.builder()
                                .totalConfirmado(((Number) result.get("totalConfirmado")).doubleValue())
                                .totalPendiente(((Number) result.get("totalPendiente")).doubleValue())
                                .totalCancelado(((Number) result.get("totalCancelado")).doubleValue())
                                .totalGeneral(((Number) result.get("totalGeneral")).doubleValue())
                                .cantidadTransacciones(((Number) result.get("totalTransacciones")).intValue())
                                .cantidadConfirmadas(((Number) result.get("countConfirmado")).intValue())
                                .cantidadPendientes(((Number) result.get("countPendiente")).intValue())
                                .cantidadCanceladas(((Number) result.get("countCancelado")).intValue())
                                .build();
        }

        // ========== IMPLEMENTACIONES NUEVOS MÉTODOS ==========

        @Override
        public Map<String, Object> comparativaMensual() {
                return jpa.comparativaMensual();
        }

        @Override
        public List<Map<String, Object>> asistenciasSemanal() {
                return jpa.asistenciasSemanal();
        }

        @Override
        public Map<String, Object> renovacionesProximas(int dias) {
                return jpa.renovacionesProximas(dias);
        }

        // ========== MÉTODOS PARA EXPORTACIÓN (USAN Application.DTO) ==========

        @Override
        public List<com.gym.backend.Reportes.Application.DTO.IngresoDetalladoDTO> obtenerIngresosDetallados(
                        com.gym.backend.Reportes.Application.DTO.FiltroIngresoDTO filtro) {
                return jpa.obtenerIngresosDetallados(filtro);
        }

        @Override
        public com.gym.backend.Reportes.Application.DTO.ResumenIngresoDTO obtenerResumenIngresos(
                        com.gym.backend.Reportes.Application.DTO.FiltroIngresoDTO filtro) {
                return jpa.obtenerResumenIngresos(filtro);
        }
}