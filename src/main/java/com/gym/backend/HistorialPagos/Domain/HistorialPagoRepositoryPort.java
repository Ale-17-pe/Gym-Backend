package com.gym.backend.HistorialPagos.Domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Puerto del repositorio de Historial de Pagos - NORMALIZADO (3NF)
 * Ya no tiene métodos que usan usuarioId directamente.
 * Para buscar por usuario, se debe hacer JOIN con pagos.
 */
public interface HistorialPagoRepositoryPort {
    HistorialPago registrar(HistorialPago historial);

    List<HistorialPago> listar();

    Page<HistorialPago> listarPaginated(Pageable pageable);

    // ELIMINADO 3NF: listarPorUsuario (ya no hay usuarioId en historial_pagos)
    // Para buscar por usuario, usar: listarPorPago con los pagos del usuario

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