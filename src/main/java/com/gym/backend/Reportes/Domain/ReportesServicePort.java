package com.gym.backend.Reportes.Domain;

import java.util.List;

public interface ReportesServicePort {

    List<IngresosMensuales> ingresosMensuales();
    List<MembresiasPorEstado> membresiasPorEstado();
    List<AsistenciasDiarias> asistenciasDiarias();
    List<TopPlanes> topPlanes();
}
