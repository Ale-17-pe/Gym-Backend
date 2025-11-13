package com.gym.backend.Planes.Domain;


import java.util.List;

public class PlanUseCase {

    private final PlanRepositoryPort repository;

    public PlanUseCase(PlanRepositoryPort repository) {
        this.repository = repository;
    }

    public Plan crear(Plan plan) {
        return repository.guardar(plan);
    }

    public Plan actualizar(Long id, Plan plan) {
        return repository.actualizar(id, plan);
    }

    public Plan obtener(Long id) {
        return repository.obtenerPorId(id);
    }

    public List<Plan> listar() {
        return repository.listar();
    }

    public void eliminar(Long id) {
        repository.eliminar(id);
    }
}
