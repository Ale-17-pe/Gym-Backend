package com.gym.backend.Reportes.Infrastructure.Controller;

import com.gym.backend.Reportes.Domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReportesController {

    private final ReportesServicePort service;

    public ReportesController(ReportesServicePort service) {
        this.service = service;
    }

    @GetMapping("/ingresos-mensuales")
    public List<IngresosMensuales> ingresos() {
        return service.ingresosMensuales();
    }

    @GetMapping("/membresias-estado")
    public List<MembresiasPorEstado> estado() {
        return service.membresiasPorEstado();
    }

    @GetMapping("/asistencias-diarias")
    public List<AsistenciasDiarias> asistencias() {
        return service.asistenciasDiarias();
    }

    @GetMapping("/top-planes")
    public List<TopPlanes> topPlanes() {
        return service.topPlanes();
    }
}
