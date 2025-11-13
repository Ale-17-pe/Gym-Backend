package com.gym.backend.Planes.Infrastructure.Adapter;

import com.gym.backend.Planes.Domain.Plan;
import com.gym.backend.Planes.Domain.PlanRepositoryPort;
import com.gym.backend.Planes.Infrastructure.Entity.PlanEntity;
import com.gym.backend.Planes.Infrastructure.Jpa.PlanJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlanRepositoryAdapter implements PlanRepositoryPort {

    private final PlanJpaRepository jpa;

    public PlanRepositoryAdapter(PlanJpaRepository jpa) {
        this.jpa = jpa;
    }

    private PlanEntity toEntity(Plan d) {
        return PlanEntity.builder()
                .id(d.getId())
                .nombrePlan(d.getNombrePlan())
                .descripcion(d.getDescripcion())
                .precio(d.getPrecio())
                .duracionDias(d.getDuracionDias())
                .activo(d.getActivo())
                .build();
    }

    private Plan toDomain(PlanEntity e) {
        return Plan.builder()
                .id(e.getId())
                .nombrePlan(e.getNombrePlan())
                .descripcion(e.getDescripcion())
                .precio(e.getPrecio())
                .duracionDias(e.getDuracionDias())
                .activo(e.getActivo())
                .build();
    }

    @Override
    public Plan guardar(Plan plan) {
        var saved = jpa.save(toEntity(plan));
        return toDomain(saved);
    }

    @Override
    public Plan actualizar(Long id, Plan plan) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalStateException("Plan no encontrado"));

        entity.setNombrePlan(plan.getNombrePlan());
        entity.setDescripcion(plan.getDescripcion());
        entity.setPrecio(plan.getPrecio());
        entity.setDuracionDias(plan.getDuracionDias());
        entity.setActivo(plan.getActivo());

        return toDomain(jpa.save(entity));
    }

    @Override
    public Plan obtenerPorId(Long id) {
        return jpa.findById(id)
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalStateException("Plan no existe"));
    }

    @Override
    public List<Plan> listar() {
        return jpa.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }
}
