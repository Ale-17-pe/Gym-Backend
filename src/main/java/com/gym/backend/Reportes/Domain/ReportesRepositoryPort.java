package com.gym.backend.Reportes.Domain;

import java.util.List;

public interface ReportesRepositoryPort {

    List<IngresosMensuales> ingresosMensuales();

    List<MembresiasPorEstado> membresiasPorEstado();

    List<AsistenciasDiarias> asistenciasDiarias();

    List<TopPlanes> topPlanes();
}
