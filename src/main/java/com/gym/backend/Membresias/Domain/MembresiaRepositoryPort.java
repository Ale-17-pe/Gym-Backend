package com.gym.backend.Membresias.Domain;

import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import com.gym.backend.Shared.Domain.BaseCrudPort;
import com.gym.backend.Shared.Domain.BaseListPort;
import com.gym.backend.Shared.Domain.BaseStatsPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Puerto del repositorio de Membresías.
 * Extiende interfaces segregadas para cumplir con ISP:
 * - BaseCrudPort: operaciones CRUD básicas
 * - BaseListPort: operaciones de listado
 * - BaseStatsPort: operaciones de estadísticas
 */
public interface MembresiaRepositoryPort extends BaseCrudPort<Membresia, Long>, BaseListPort<Membresia>, BaseStatsPort {

    // ==================== BÚSQUEDAS ESPECÍFICAS ====================

    Optional<Membresia> buscarActivaPorUsuario(Long usuarioId);

    Optional<Membresia> buscarPorCodigoAcceso(String codigoAcceso);

    // ==================== LISTADOS POR FILTRO ====================

    List<Membresia> listarPorUsuario(Long usuarioId);

    Page<Membresia> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable);

    List<Membresia> listarPorEstado(EstadoMembresia estado);

    Page<Membresia> listarPorEstadoPaginated(EstadoMembresia estado, Pageable pageable);

    List<Membresia> listarPorVencer();

    List<Membresia> buscarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);

    // ==================== ESTADÍSTICAS ====================

    Long contarPorEstado(EstadoMembresia estado);

    Long contarPorVencer();

    Long contarPorUsuario(Long usuarioId);
}