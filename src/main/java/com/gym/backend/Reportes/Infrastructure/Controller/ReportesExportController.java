package com.gym.backend.Reportes.Infrastructure.Controller;

import com.gym.backend.Reportes.Domain.ReportesUseCase;
import com.gym.backend.Reportes.Infrastructure.Export.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/reportes/export")
@RequiredArgsConstructor
public class ReportesExportController {

    private final ReportesUseCase useCase;

    // Exportadores
    private final ReportePagosMetodoPDF pagosMetodoPDF;
    private final ReportePagosMetodoExcel pagosMetodoExcel;

    private final ReporteUsuariosNuevosPDF usuariosNuevosPDF;
    private final ReporteUsuariosNuevosExcel usuariosNuevosExcel;

    private final ReporteTopPlanesPDF topPlanesPDF;
    private final ReporteTopPlanesExcel topPlanesExcel;


    // --------------------------------------------------------
    // 📌 1) PAGOS POR MÉTODO
    // --------------------------------------------------------

    @GetMapping("/pagos-metodo/pdf")
    public ResponseEntity<byte[]> exportPagosMetodoPDF() throws Exception {
        var data = useCase.pagosPorMetodo();
        byte[] pdf = pagosMetodoPDF.generar(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pagos_por_metodo.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/pagos-metodo/excel")
    public ResponseEntity<byte[]> exportPagosMetodoExcel() throws Exception {
        var data = useCase.pagosPorMetodo();
        byte[] excel = pagosMetodoExcel.generar(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pagos_por_metodo.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }


    // --------------------------------------------------------
    // 📌 2) USUARIOS NUEVOS POR MES
    // --------------------------------------------------------

    @GetMapping("/usuarios-nuevos/pdf")
    public ResponseEntity<byte[]> exportUsuariosNuevosPDF() throws Exception {
        var data = useCase.usuariosNuevosPorMes();
        byte[] pdf = usuariosNuevosPDF.generar(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios_nuevos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/usuarios-nuevos/excel")
    public ResponseEntity<byte[]> exportUsuariosNuevosExcel() throws Exception {
        var data = useCase.usuariosNuevosPorMes();
        byte[] excel = usuariosNuevosExcel.generar(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios_nuevos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }


    // --------------------------------------------------------
    // 📌 3) TOP PLANES
    // --------------------------------------------------------

    @GetMapping("/top-planes/pdf")
    public ResponseEntity<byte[]> exportTopPlanesPDF() throws Exception {
        var data = useCase.topPlanes();
        byte[] pdf = topPlanesPDF.generar(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=top_planes.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/top-planes/excel")
    public ResponseEntity<byte[]> exportTopPlanesExcel() throws Exception {
        var data = useCase.topPlanes();
        byte[] excel = topPlanesExcel.generar(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=top_planes.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }


}
