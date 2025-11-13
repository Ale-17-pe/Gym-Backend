package com.gym.backend.Planes.Domain;

import java.util.List;

public interface PlanRepositoryPort {

    Plan guardar(Plan plan);

    Plan actualizar(Long id, Plan plan);

    Plan obtenerPorId(Long id);

    List<Plan> listar();

    void eliminar(Long id);
}
