package com.gym.backend.Planes.Application.Mapper;

import com.gym.backend.Planes.Application.Dto.CrearPlanRequest;
import com.gym.backend.Planes.Application.Dto.PlanDTO;
import com.gym.backend.Planes.Application.Dto.PlanResponse;
import com.gym.backend.Planes.Domain.Plan;
import com.gym.backend.Planes.Infrastructure.Entity.PlanEntity;
import org.mapstruct.*;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PlanMapper {

    Plan toDomain(PlanEntity entity);
    PlanEntity toEntity(Plan domain);

    // Domain <-> DTO
    PlanDTO toDTO(Plan domain);
    Plan toDomain(PlanDTO dto);

    // Para creación
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", constant = "true")
    Plan toDomainFromCreateRequest(CrearPlanRequest request);

    // Para respuesta con campo calculado
    @Mapping(target = "precioMensual", expression = "java(plan.calcularPrecioMensual())")
    PlanResponse toResponse(Plan plan);

    // Para actualizaciones
    @Mapping(target = "id", ignore = true)
    void updateDomainFromDTO(PlanDTO dto, @MappingTarget Plan domain);
}

