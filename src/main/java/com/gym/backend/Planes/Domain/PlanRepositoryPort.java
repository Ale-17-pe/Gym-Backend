package com.gym.backend.Planes.Domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PlanRepositoryPort {
    Plan guardar(Plan plan);
    Plan actualizar(Plan plan);
    Optional<Plan> buscarPorId(Long id);
    Optional<Plan> buscarPorNombre(String nombrePlan);
    List<Plan> listar();
    Page<Plan> listarPaginated(Pageable pageable);
    List<Plan> listarActivos();
    Page<Plan> listarActivosPaginated(Pageable pageable);
    List<Plan> listarInactivos();
    List<Plan> buscarPorCategoria(String categoria);
    List<Plan> buscarDestacados();
    List<Plan> buscarPorPrecioMenorIgual(Double precioMax);
    Page<Plan> buscarPorRangoPrecio(Double precioMin, Double precioMax, Pageable pageable);

    // Métodos para analytics
    Long contarPlanesActivos();
    Double obtenerPrecioPromedio();
    Plan obtenerPlanMasContratado();

    void eliminar(Long id);
}