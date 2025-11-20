package com.gym.backend.HistorialPagos.Domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface HistorialPagoRepositoryPort {
    HistorialPago registrar(HistorialPago historial);
    List<HistorialPago> listar();
    Page<HistorialPago> listarPaginated(Pageable pageable);
    List<HistorialPago> listarPorUsuario(Long usuarioId);
    Page<HistorialPago> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable);
    List<HistorialPago> listarPorPago(Long pagoId);
    List<HistorialPago> listarPorEstado(String estado);
    List<HistorialPago> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);
    HistorialPago obtenerUltimoCambio(Long pagoId);
    List<HistorialPago> obtenerCambiosRecientes(int limite);

    // Métodos para estadísticas
    Long contarTotal();
    Long contarCambiosHoy();
    Long contarPorEstadoNuevo(String estado);
    Long contarCambiosPorMes(int año, int mes);
    Long contarConfirmacionesPorMes(int año, int mes);
    Long contarRechazosPorMes(int año, int mes);
}