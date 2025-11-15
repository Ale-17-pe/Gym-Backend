package com.gym.backend.Planes.Infrastructure.Adapter;

import com.gym.backend.Planes.Domain.Plan;
import com.gym.backend.Planes.Domain.PlanRepositoryPort;
import com.gym.backend.Planes.Infrastructure.Entity.PlanEntity;
import com.gym.backend.Planes.Infrastructure.Jpa.PlanJpaRepository;
import lombok.RequiredArgsConstructor;
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
        PlanEntity existente = jpa.findById(plan.getId())
                .orElseThrow(() -> new RuntimeException("Plan no encontrado para actualizar"));

        existente.setNombrePlan(plan.getNombrePlan());
        existente.setDescripcion(plan.getDescripcion());
        existente.setPrecio(plan.getPrecio());
        existente.setDuracionDias(plan.getDuracionDias());
        existente.setActivo(plan.getActivo());
        existente.setBeneficios(plan.getBeneficios());

        return toDomain(jpa.save(existente));
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
    public List<Plan> listarActivos() {
        return jpa.findByActivoTrue().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Plan> listarInactivos() {
        return jpa.findByActivoFalse().stream().map(this::toDomain).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
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
                .build();
    }
}