package com.gym.backend.Planes.Domain;

import com.gym.backend.Shared.Domain.BaseCrudPort;
import com.gym.backend.Shared.Domain.BaseListPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del repositorio de Planes.
 * Extiende interfaces segregadas para cumplir con ISP:
 * - BaseCrudPort: operaciones CRUD básicas
 * - BaseListPort: operaciones de listado
 */
public interface PlanRepositoryPort extends BaseCrudPort<Plan, Long>, BaseListPort<Plan> {

    // ==================== BÚSQUEDAS ESPECÍFICAS ====================

    Optional<Plan> buscarPorNombre(String nombrePlan);

    // ==================== LISTADOS POR FILTRO ====================

    List<Plan> listarActivos();

    Page<Plan> listarActivosPaginated(Pageable pageable);

    List<Plan> listarInactivos();

    List<Plan> buscarPorCategoria(String categoria);

    List<Plan> buscarDestacados();

    List<Plan> buscarPorPrecioMenorIgual(Double precioMax);

    Page<Plan> buscarPorRangoPrecio(Double precioMin, Double precioMax, Pageable pageable);

    // ==================== ANALYTICS ====================

    Long contarPlanesActivos();

    Double obtenerPrecioPromedio();

    Plan obtenerPlanMasContratado();

    // ==================== OPERACIONES DE ELIMINACIÓN ====================

    void eliminar(Long id);
}