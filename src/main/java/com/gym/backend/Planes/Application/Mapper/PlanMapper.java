package com.gym.backend.Planes.Application.Mapper;

import com.gym.backend.Planes.Application.Dto.ActualizarPlanRequest;
import com.gym.backend.Planes.Application.Dto.CrearPlanRequest;
import com.gym.backend.Planes.Application.Dto.PlanDTO;
import com.gym.backend.Planes.Application.Dto.PlanResponse;
import com.gym.backend.Planes.Domain.Plan;
import com.gym.backend.Planes.Infrastructure.Entity.PlanEntity;
import org.mapstruct.*;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PlanMapper {
    Plan toDomain(PlanEntity entity);
    PlanEntity toEntity(Plan domain);
    PlanDTO toDTO(Plan domain);
    Plan toDomain(PlanDTO dto);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", constant = "true")
    @Mapping(target = "vecesContratado", constant = "0")
    @Mapping(target = "ratingPromedio", constant = "0.0")
    @Mapping(target = "destacado", constant = "false")
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "fechaActualizacion", expression = "java(java.time.LocalDateTime.now())")
    Plan toDomainFromCreateRequest(CrearPlanRequest request);
    @Mapping(target = "precioMensual", expression = "java(plan.calcularPrecioMensual())")
    PlanResponse toResponse(Plan plan);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vecesContratado", ignore = true)
    @Mapping(target = "ratingPromedio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", expression = "java(java.time.LocalDateTime.now())")
    void updateDomainFromActualizarRequest(ActualizarPlanRequest request, @MappingTarget Plan domain);
}
