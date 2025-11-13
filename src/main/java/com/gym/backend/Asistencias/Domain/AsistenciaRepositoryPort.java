package com.gym.backend.Asistencias.Domain;

import java.time.LocalDateTime;
import java.util.List;

public interface AsistenciaRepositoryPort {

    Asistencia registrar(Asistencia asistencia);

    List<Asistencia> listar();

    List<Asistencia> listarPorUsuario(Long usuarioId);

    boolean existeAsistenciaHoy(Long usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}