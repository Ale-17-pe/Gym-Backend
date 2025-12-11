package com.gym.backend.HistorialMembresias.Domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Puerto del repositorio de Historial de Membresías - NORMALIZADO (3NF)
 * Ya no tiene métodos que usan usuarioId directamente.
 * Para buscar por usuario, se debe hacer JOIN con membresias.
 */
public interface HistorialMembresiaRepositoryPort {
    HistorialMembresia registrar(HistorialMembresia historial);

    List<HistorialMembresia> listar();

    Page<HistorialMembresia> listarPaginated(Pageable pageable);

    // ELIMINADO 3NF: listarPorUsuario (ya no hay usuarioId en historial_membresias)
    // Para buscar por usuario, usar: listarPorMembresia con las membresías del
    // usuario

    List<HistorialMembresia> listarPorMembresia(Long membresiaId);

    List<HistorialMembresia> listarPorAccion(String accion);

    List<HistorialMembresia> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);

    HistorialMembresia obtenerUltimoCambio(Long membresiaId);

    List<HistorialMembresia> obtenerCambiosRecientes(int limite);

    // Métodos para estadísticas
    Long contarTotal();

    Long contarCambiosHoy();

    Long contarPorAccion(String accion);

    Long contarCambiosPorMes(int año, int mes);

    Long contarCreacionesPorMes(int año, int mes);

    Long contarExtensionesPorMes(int año, int mes);
}