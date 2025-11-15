package com.gym.backend.Planes.Domain;

import java.util.List;
import java.util.Optional;

public interface PlanRepositoryPort {
    Plan guardar(Plan plan);
    Plan actualizar(Plan plan);
    Optional<Plan> buscarPorId(Long id);
    Optional<Plan> buscarPorNombre(String nombrePlan);
    List<Plan> listar();
    List<Plan> listarActivos();
    List<Plan> listarInactivos();
    void eliminar(Long id);
}
