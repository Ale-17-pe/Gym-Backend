package com.gym.backend.Reportes.Infrastructure.Adapter;

import com.gym.backend.Reportes.Domain.*;
import com.gym.backend.Reportes.Infrastructure.Jpa.ReportesJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportesRepositoryAdapter implements ReportesRepositoryPort {

    private final ReportesJpaRepository jpa;

    public ReportesRepositoryAdapter(ReportesJpaRepository jpa) {
        this.jpa = jpa;
    }

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
                .map(r -> new TopPlanes((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }
}
