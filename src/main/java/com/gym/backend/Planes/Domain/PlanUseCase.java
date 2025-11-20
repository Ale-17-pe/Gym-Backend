package com.gym.backend.Planes.Domain;


import com.gym.backend.Planes.Application.Dto.ActualizarPlanRequest;
import com.gym.backend.Planes.Domain.Exceptions.PlanDuplicateException;
import com.gym.backend.Planes.Domain.Exceptions.PlanNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlanUseCase {

    private final PlanRepositoryPort repository;

    public Plan crear(Plan plan) {
        log.info("Creando nuevo plan: {}", plan.getNombrePlan());
        plan.validar();

        if (repository.buscarPorNombre(plan.getNombrePlan()).isPresent()) {
            throw new PlanDuplicateException(plan.getNombrePlan());
        }

        return repository.guardar(plan);
    }

    public Plan actualizar(Long id, ActualizarPlanRequest request) {
        log.info("Actualizando plan ID: {}", id);
        Plan existente = obtener(id);

        // Actualizar solo campos permitidos
        if (request.getNombrePlan() != null) {
            // Verificar duplicado solo si cambia el nombre
            repository.buscarPorNombre(request.getNombrePlan())
                    .ifPresent(planConNombre -> {
                        if (!planConNombre.getId().equals(id)) {
                            throw new PlanDuplicateException(request.getNombrePlan());
                        }
                    });
            existente.setNombrePlan(request.getNombrePlan());
        }

        if (request.getDescripcion() != null) {
            existente.setDescripcion(request.getDescripcion());
        }
        if (request.getPrecio() != null) {
            existente.setPrecio(request.getPrecio());
        }
        if (request.getDuracionDias() != null) {
            existente.setDuracionDias(request.getDuracionDias());
        }
        if (request.getBeneficios() != null) {
            existente.setBeneficios(request.getBeneficios());
        }
        if (request.getActivo() != null) {
            existente.setActivo(request.getActivo());
        }
        if (request.getDestacado() != null) {
            existente.setDestacado(request.getDestacado());
        }
        if (request.getCategoria() != null) {
            existente.setCategoria(request.getCategoria());
        }

        existente.validar();
        return repository.actualizar(existente);
    }

    @Transactional(readOnly = true)
    public Plan obtener(Long id) {
        log.debug("Obteniendo plan ID: {}", id);
        return repository.buscarPorId(id)
                .orElseThrow(() -> new PlanNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Plan> listar() {
        return repository.listar();
    }

    @Transactional(readOnly = true)
    public List<Plan> listarActivos() {
        return repository.listarActivos();
    }

    @Transactional(readOnly = true)
    public List<Plan> listarInactivos() {
        return repository.listarInactivos();
    }

    @Transactional(readOnly = true)
    public Page<Plan> listarPaginated(Pageable pageable) {
        return repository.listarPaginated(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Plan> listarActivosPaginated(Pageable pageable) {
        return repository.listarActivosPaginated(pageable);
    }

    @Transactional(readOnly = true)
    public List<Plan> buscarPorCategoria(String categoria) {
        return repository.buscarPorCategoria(categoria);
    }

    @Transactional(readOnly = true)
    public List<Plan> buscarDestacados() {
        return repository.buscarDestacados();
    }

    @Transactional(readOnly = true)
    public List<Plan> buscarPorPrecioMenorIgual(Double precioMax) {
        return repository.buscarPorPrecioMenorIgual(precioMax);
    }

    @Transactional(readOnly = true)
    public Page<Plan> buscarPorRangoPrecio(Double precioMin, Double precioMax, Pageable pageable) {
        return repository.buscarPorRangoPrecio(precioMin, precioMax, pageable);
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

    public void incrementarContrataciones(Long planId) {
        Plan plan = obtener(planId);
        plan.incrementarContrataciones();
        repository.actualizar(plan);
    }

    public void actualizarRating(Long planId, Double nuevoRating) {
        Plan plan = obtener(planId);
        plan.actualizarRating(nuevoRating);
        repository.actualizar(plan);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPlanesActivos", repository.contarPlanesActivos());
        stats.put("precioPromedio", repository.obtenerPrecioPromedio());
        stats.put("planMasContratado", repository.obtenerPlanMasContratado());
        return stats;
    }
}
