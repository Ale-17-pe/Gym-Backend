package com.gym.backend.Reportes.Domain;

import com.gym.backend.Reportes.Domain.Record.AsistenciasDiarias;
import com.gym.backend.Reportes.Domain.Record.IngresosMensuales;
import com.gym.backend.Reportes.Domain.Record.MembresiasPorEstado;
import com.gym.backend.Reportes.Domain.Record.TopPlanes;

import java.util.List;

public interface ReportesServicePort {

    List<IngresosMensuales> ingresosMensuales();
    List<MembresiasPorEstado> membresiasPorEstado();
    List<AsistenciasDiarias> asistenciasDiarias();
    List<TopPlanes> topPlanes();
}
