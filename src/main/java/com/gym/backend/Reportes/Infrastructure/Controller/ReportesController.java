package com.gym.backend.Reportes.Infrastructure.Controller;

import com.gym.backend.Reportes.Domain.*;
import com.gym.backend.Reportes.Domain.Record.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReportesController {

    private final ReportesUseCase useCase;

    @GetMapping("/ingresos-mensuales")
    public List<IngresosMensuales> ingresosMensuales() {
        return useCase.ingresosMensuales();
    }

    @GetMapping("/membresias-estado")
    public List<MembresiasPorEstado> membresiasPorEstado() {
        return useCase.membresiasPorEstado();
    }

    @GetMapping("/asistencias-diarias")
    public List<AsistenciasDiarias> asistenciasDiarias() {
        return useCase.asistenciasDiarias();
    }

    @GetMapping("/top-planes")
    public List<TopPlanes> topPlanes() {
        return useCase.topPlanes();
    }

    @GetMapping("/ingresos-rango")
    public List<IngresosMensuales> ingresosPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return useCase.ingresosPorRango(inicio, fin);
    }

    @GetMapping("/asistencias-rango")
    public List<AsistenciasDiarias> asistenciasPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return useCase.asistenciasPorRango(inicio, fin);
    }

    @GetMapping("/usuarios-nuevos")
    public List<UsuariosNuevos> usuariosNuevosPorMes() {
        return useCase.usuariosNuevosPorMes();
    }

    @GetMapping("/membresias-plan")
    public List<MembresiasPorPlan> membresiasPorPlan() {
        return useCase.membresiasPorPlan();
    }

    @GetMapping("/pagos-metodo")
    public List<PagosPorMetodo> pagosPorMetodo() {
        return useCase.pagosPorMetodo();
    }

    @GetMapping("/asistencias-hora")
    public List<AsistenciasPorHora> asistenciasPorHora(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return useCase.asistenciasPorHora(fecha);
    }

    @GetMapping("/rendimiento-mensual")
    public List<RendimientoMensual> rendimientoMensual() {
        return useCase.rendimientoMensual();
    }

    @GetMapping("/usuarios-activos")
    public List<UsuariosActivos> usuariosMasActivos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return useCase.usuariosMasActivos(inicio, fin);
    }

    @GetMapping("/planes-populares")
    public List<PlanesPopulares> planesPopulares() {
        return useCase.planesPopulares();
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        // Reporte consolidado para dashboard
        return Map.of(
                "estadisticas", useCase.estadisticasGenerales(),
                "topPlanes", useCase.topPlanes(),
                "ingresosMensuales", useCase.ingresosMensuales(),
                "asistenciasDiarias", useCase.asistenciasDiarias());
    }

    // ========== NUEVOS ENDPOINTS ==========

    @GetMapping("/comparativa-mensual")
    public ResponseEntity<Map<String, Object>> comparativaMensual() {
        return ResponseEntity.ok(useCase.comparativaMensual());
    }

    @GetMapping("/asistencias-semanal")
    public ResponseEntity<List<Map<String, Object>>> asistenciasSemanal() {
        return ResponseEntity.ok(useCase.asistenciasSemanal());
    }

    @GetMapping("/renovaciones-proximas")
    public ResponseEntity<Map<String, Object>> renovacionesProximas(
            @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(useCase.renovacionesProximas(dias));
    }
}