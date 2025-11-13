package com.gym.backend.Planes.Application.Dto;


import com.gym.backend.Planes.Domain.Plan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    PlanDTO toDTO(Plan plan);

    Plan toDomain(PlanDTO dto);
}
