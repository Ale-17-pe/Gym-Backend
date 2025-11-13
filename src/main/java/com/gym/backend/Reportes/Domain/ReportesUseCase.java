package com.gym.backend.Reportes.Domain;

import java.util.List;

public class ReportesUseCase {

    private final ReportesRepositoryPort repo;

    public ReportesUseCase(ReportesRepositoryPort repo) {
        this.repo = repo;
    }

    public List<IngresosMensuales> ingresosMensuales() {
        return repo.ingresosMensuales();
    }

    public List<MembresiasPorEstado> membresiasPorEstado() {
        return repo.membresiasPorEstado();
    }

    public List<AsistenciasDiarias> asistenciasDiarias() {
        return repo.asistenciasDiarias();
    }

    public List<TopPlanes> topPlanes() {
        return repo.topPlanes();
    }
}