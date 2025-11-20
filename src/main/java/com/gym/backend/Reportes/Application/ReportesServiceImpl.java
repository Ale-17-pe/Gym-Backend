package com.gym.backend.Reportes.Application;

import com.gym.backend.Reportes.Domain.*;
import com.gym.backend.Reportes.Domain.Record.AsistenciasDiarias;
import com.gym.backend.Reportes.Domain.Record.IngresosMensuales;
import com.gym.backend.Reportes.Domain.Record.MembresiasPorEstado;
import com.gym.backend.Reportes.Domain.Record.TopPlanes;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportesServiceImpl implements ReportesServicePort {

    private final ReportesRepositoryPort repo;

    public ReportesServiceImpl(ReportesRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public List<IngresosMensuales> ingresosMensuales() {
        return repo.ingresosMensuales();
    }

    @Override
    public List<MembresiasPorEstado> membresiasPorEstado() {
        return repo.membresiasPorEstado();
    }

    @Override
    public List<AsistenciasDiarias> asistenciasDiarias() {
        return repo.asistenciasDiarias();
    }

    @Override
    public List<TopPlanes> topPlanes() {
        return repo.topPlanes();
    }
}
