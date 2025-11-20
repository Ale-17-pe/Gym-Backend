package com.gym.backend.Asistencias.Application.Mapper;

import com.gym.backend.Asistencias.Application.Dto.AsistenciaDTO;
import com.gym.backend.Asistencias.Application.Dto.AsistenciaResponse;
import com.gym.backend.Asistencias.Domain.Asistencia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AsistenciaMapper {

    AsistenciaDTO toDTO(Asistencia asistencia);
    Asistencia toDomain(AsistenciaDTO dto);

    @Mapping(target = "fecha", source = "fechaHora", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "hora", source = "fechaHora", dateFormat = "HH:mm:ss")
    @Mapping(target = "mensaje", ignore = true)
    AsistenciaResponse toResponse(Asistencia asistencia);
}