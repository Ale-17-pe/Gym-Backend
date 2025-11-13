package com.gym.backend.Asistencias.Application.Mapper;

import com.gym.backend.Asistencias.Application.Dto.AsistenciaDTO;
import com.gym.backend.Asistencias.Domain.Asistencia;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AsistenciaMapper {

    AsistenciaDTO toDTO(Asistencia asistencia);

    Asistencia toDomain(AsistenciaDTO dto);
}
