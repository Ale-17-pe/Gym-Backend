package com.gym.backend.HistorialMembresias.Application.Mapper;

import com.gym.backend.HistorialMembresias.Application.Dto.HistorialMembresiaDTO;
import com.gym.backend.HistorialMembresias.Domain.HistorialMembresia;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HistorialMembresiaMapper {

    HistorialMembresiaDTO toDTO(HistorialMembresia historial);

    HistorialMembresia toDomain(HistorialMembresiaDTO dto);
}