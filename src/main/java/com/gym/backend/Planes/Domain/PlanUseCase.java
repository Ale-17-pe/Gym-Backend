package com.gym.backend.Planes.Domain;


import com.gym.backend.Planes.Domain.Exceptions.PlanDuplicateException;
import com.gym.backend.Planes.Domain.Exceptions.PlanNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanUseCase {

    private final PlanRepositoryPort repository;

    public Plan crear(Plan plan) {
        plan.validar();

        // Verificar que no exista un plan con el mismo nombre
        if (repository.buscarPorNombre(plan.getNombrePlan()).isPresent()) {
            throw new PlanDuplicateException(plan.getNombrePlan());
        }

        return repository.guardar(plan);
    }

    public Plan actualizar(Plan plan) {
        Plan existente = obtener(plan.getId());
        plan.validar();

        // Verificar que el nombre no esté duplicado (excluyendo el actual)
        repository.buscarPorNombre(plan.getNombrePlan())
                .ifPresent(planConNombre -> {
                    if (!planConNombre.getId().equals(plan.getId())) {
                        throw new PlanDuplicateException(plan.getNombrePlan());
                    }
                });

        return repository.actualizar(plan);
    }

    public Plan obtener(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new PlanNotFoundException(id));
    }

    public List<Plan> listar() {
        return repository.listar();
    }

    public List<Plan> listarActivos() {
        return repository.listarActivos();
    }

    public List<Plan> listarInactivos() {
        return repository.listarInactivos();
    }

    public void eliminar(Long id) {
        obtener(id); // Verificar que existe
        repository.eliminar(id);
    }

    public Plan desactivar(Long id) {
        Plan plan = obtener(id);
        plan.desactivar();
        return repository.actualizar(plan);
    }

    public Plan activar(Long id) {
        Plan plan = obtener(id);
        plan.activar();
        return repository.actualizar(plan);
    }
}
