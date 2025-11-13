package com.gym.backend.Reportes.Infrastructure.Controller;

import com.gym.backend.Reportes.Domain.*;
import com.gym.backend.Reportes.Infrastructure.Adapter.ReportesRepositoryAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReportesController {

    private final ReportesUseCase useCase;

    public ReportesController(ReportesRepositoryAdapter adapter) {
        this.useCase = new ReportesUseCase(adapter);
    }

    @GetMapping("/ingresos-mensuales")
    public List<IngresosMensuales> ingresosMensuales() {
        return useCase.ingresosMensuales();
    }

    @GetMapping("/membresias-estado")
    public List<MembresiasPorEstado> membresiasEstado() {
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
}