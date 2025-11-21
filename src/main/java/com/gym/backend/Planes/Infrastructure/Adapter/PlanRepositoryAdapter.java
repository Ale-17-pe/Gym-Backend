package com.gym.backend.Planes.Infrastructure.Adapter;

import com.gym.backend.Planes.Domain.Plan;
import com.gym.backend.Planes.Domain.PlanRepositoryPort;
import com.gym.backend.Planes.Infrastructure.Entity.PlanEntity;
import com.gym.backend.Planes.Infrastructure.Jpa.PlanJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlanRepositoryAdapter implements PlanRepositoryPort {

    private final PlanJpaRepository jpa;

    @Override
    public Plan guardar(Plan plan) {
        PlanEntity entity = toEntity(plan);
        return toDomain(jpa.save(entity));
    }

    @Override
    public Plan actualizar(Plan plan) {
        return jpa.findById(plan.getId())
                .map(existente -> {
                    actualizarEntityDesdeDomain(existente, plan);
                    return toDomain(jpa.save(existente));
                })
                .orElseThrow(() -> new RuntimeException("Plan no encontrado para actualizar"));
    }

    @Override
    public Optional<Plan> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Plan> buscarPorNombre(String nombrePlan) {
        return jpa.findByNombrePlan(nombrePlan).map(this::toDomain);
    }

    @Override
    public List<Plan> listar() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Plan> listarPaginated(Pageable pageable) {
        return jpa.findAll(pageable).map(this::toDomain);
    }

    @Override
    public List<Plan> listarActivos() {
        return jpa.findByActivoTrue().stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Plan> listarActivosPaginated(Pageable pageable) {
        return jpa.findByActivoTrue(pageable).map(this::toDomain);
    }

    @Override
    public List<Plan> listarInactivos() {
        return jpa.findByActivoFalse().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Plan> buscarPorCategoria(String categoria) {
        return jpa.findByCategoria(categoria).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Plan> buscarDestacados() {
        return jpa.findByDestacadoTrueAndActivoTrue().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Plan> buscarPorPrecioMenorIgual(Double precioMax) {
        return jpa.findByPrecioLessThanEqual(precioMax).stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Plan> buscarPorRangoPrecio(Double precioMin, Double precioMax, Pageable pageable) {
        return jpa.findByPrecioBetweenAndActivoTrue(precioMin, precioMax, pageable)
                .map(this::toDomain);
    }

    @Override
    public Long contarPlanesActivos() {
        return jpa.countByActivoTrue();
    }

    @Override
    public Double obtenerPrecioPromedio() {
        return jpa.findPrecioPromedioByActivoTrue();
    }

    @Override
    public Plan obtenerPlanMasContratado() {
        return jpa.findTopByOrderByVecesContratadoDesc()
                .map(this::toDomain)
                .orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    private void actualizarEntityDesdeDomain(PlanEntity entity, Plan domain) {
        if (domain.getNombrePlan() != null) {
            entity.setNombrePlan(domain.getNombrePlan());
        }
        if (domain.getDescripcion() != null) {
            entity.setDescripcion(domain.getDescripcion());
        }
        if (domain.getPrecio() != null) {
            entity.setPrecio(domain.getPrecio());
        }
        if (domain.getDuracionDias() != null) {
            entity.setDuracionDias(domain.getDuracionDias());
        }
        if (domain.getActivo() != null) {
            entity.setActivo(domain.getActivo());
        }
        if (domain.getBeneficios() != null) {
            entity.setBeneficios(domain.getBeneficios());
        }
        if (domain.getVecesContratado() != null) {
            entity.setVecesContratado(domain.getVecesContratado());
        }
        if (domain.getRatingPromedio() != null) {
            entity.setRatingPromedio(domain.getRatingPromedio());
        }
        if (domain.getDestacado() != null) {
            entity.setDestacado(domain.getDestacado());
        }
        if (domain.getCategoria() != null) {
            entity.setCategoria(domain.getCategoria());
        }
    }

    private Plan toDomain(PlanEntity entity) {
        return Plan.builder()
                .id(entity.getId())
                .nombrePlan(entity.getNombrePlan())
                .descripcion(entity.getDescripcion())
                .precio(entity.getPrecio())
                .duracionDias(entity.getDuracionDias())
                .activo(entity.getActivo())
                .beneficios(entity.getBeneficios())
                .vecesContratado(entity.getVecesContratado())
                .ratingPromedio(entity.getRatingPromedio())
                .destacado(entity.getDestacado())
                .categoria(entity.getCategoria())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private PlanEntity toEntity(Plan domain) {
        return PlanEntity.builder()
                .id(domain.getId())
                .nombrePlan(domain.getNombrePlan())
                .descripcion(domain.getDescripcion())
                .precio(domain.getPrecio())
                .duracionDias(domain.getDuracionDias())
                .activo(domain.getActivo())
                .beneficios(domain.getBeneficios())
                .vecesContratado(domain.getVecesContratado())
                .ratingPromedio(domain.getRatingPromedio())
                .destacado(domain.getDestacado())
                .categoria(domain.getCategoria())
                .fechaCreacion(domain.getFechaCreacion())
                .fechaActualizacion(domain.getFechaActualizacion())
                .build();
    }
}