package com.gym.backend.Reportes.Infrastructure.Controller;

import com.gym.backend.Reportes.Application.DTO.*;
import com.gym.backend.Reportes.Application.Service.ReportesExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reportes/export")
@RequiredArgsConstructor
public class ExportController {

    private final ReportesExportService exportService;

    // ========================================
    // REPORTE DETALLADO DE INGRESOS
    // ========================================

    @PostMapping("/ingresos/detallado")
    public ResponseEntity<List<IngresoDetalladoDTO>> obtenerIngresosDetallados(
            @RequestBody FiltroIngresoDTO filtro) {
        log.info("Obteniendo ingresos detallados con filtro: {}", filtro);
        return ResponseEntity.ok(exportService.obtenerIngresosDetallados(filtro));
    }

    @PostMapping("/ingresos/resumen")
    public ResponseEntity<ResumenIngresoDTO> obtenerResumenIngresos(
            @RequestBody FiltroIngresoDTO filtro) {
        log.info("Obteniendo resumen de ingresos con filtro: {}", filtro);
        return ResponseEntity.ok(exportService.obtenerResumenIngresos(filtro));
    }

    @PostMapping("/ingresos/detallado/pdf")
    public ResponseEntity<byte[]> descargarIngresosDetalladoPDF(
            @RequestBody FiltroIngresoDTO filtro) {
        log.info("Descargando PDF de ingresos detallados");
        byte[] pdf = exportService.generarIngresosPDF(filtro);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_ingresos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/ingresos/detallado/excel")
    public ResponseEntity<byte[]> descargarIngresosDetalladoExcel(
            @RequestBody FiltroIngresoDTO filtro) {
        log.info("Descargando Excel de ingresos detallados");
        byte[] excel = exportService.generarIngresosExcel(filtro);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_ingresos.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    // ========================================
    // PAGOS POR MÉTODO
    // ========================================

    @GetMapping("/pagos-metodo/pdf")
    public ResponseEntity<byte[]> descargarPagosMetodoPDF() {
        log.info("Descargando PDF de pagos por método");
        byte[] pdf = exportService.generarPagosMetodoPDF();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pagos_por_metodo.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/pagos-metodo/excel")
    public ResponseEntity<byte[]> descargarPagosMetodoExcel() {
        log.info("Descargando Excel de pagos por método");
        byte[] excel = exportService.generarPagosMetodoExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pagos_por_metodo.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    // ========================================
    // USUARIOS NUEVOS
    // ========================================

    @GetMapping("/usuarios-nuevos/pdf")
    public ResponseEntity<byte[]> descargarUsuariosNuevosPDF() {
        log.info("Descargando PDF de usuarios nuevos");
        byte[] pdf = exportService.generarUsuariosNuevosPDF();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios_nuevos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/usuarios-nuevos/excel")
    public ResponseEntity<byte[]> descargarUsuariosNuevosExcel() {
        log.info("Descargando Excel de usuarios nuevos");
        byte[] excel = exportService.generarUsuariosNuevosExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios_nuevos.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    // ========================================
    // TOP PLANES
    // ========================================

    @GetMapping("/top-planes/pdf")
    public ResponseEntity<byte[]> descargarTopPlanesPDF() {
        log.info("Descargando PDF de top planes");
        byte[] pdf = exportService.generarTopPlanesPDF();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=top_planes.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/top-planes/excel")
    public ResponseEntity<byte[]> descargarTopPlanesExcel() {
        log.info("Descargando Excel de top planes");
        byte[] excel = exportService.generarTopPlanesExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=top_planes.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }
}
