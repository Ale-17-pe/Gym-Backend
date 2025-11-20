package com.gym.backend.Asistencias.Domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepositoryPort {
    Asistencia registrar(Asistencia asistencia);
    Asistencia actualizar(Asistencia asistencia);
    Optional<Asistencia> buscarPorId(Long id);
    List<Asistencia> listar();
    Page<Asistencia> listarPaginated(Pageable pageable);
    List<Asistencia> listarPorUsuario(Long usuarioId);
    Page<Asistencia> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable);
    List<Asistencia> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);

    // Métodos específicos para validaciones
    boolean existeEntradaHoy(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);
    boolean existeSalidaHoy(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);
    Optional<Asistencia> buscarEntradaHoy(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);

    // Métodos para estadísticas
    Long contarTotal();
    Long contarAsistenciasHoy();
    Long contarUsuariosActivosHoy();
    Long contarPromedioMensual();
    Long contarEntradasPorMes(int año, int mes);
    Long contarSalidasPorMes(int año, int mes);
    Long contarUsuariosUnicosPorMes(int año, int mes);
    int contarAsistenciasMes(Long usuarioId, int año, int mes);
}