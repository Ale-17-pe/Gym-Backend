package com.gym.backend.HistorialMembresias.Application.Mapper;

import com.gym.backend.HistorialMembresias.Application.Dto.HistorialMembresiaDTO;
import com.gym.backend.HistorialMembresias.Application.Dto.HistorialMembresiaResponse;
import com.gym.backend.HistorialMembresias.Domain.HistorialMembresia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistorialMembresiaMapper {

    HistorialMembresiaDTO toDTO(HistorialMembresia historial);
    HistorialMembresia toDomain(HistorialMembresiaDTO dto);

    @Mapping(target = "fecha", source = "fechaCambio", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "hora", source = "fechaCambio", dateFormat = "HH:mm:ss")
    @Mapping(target = "tipoCambio", expression = "java(historial.obtenerTipoCambio())")
    @Mapping(target = "mensaje", ignore = true)
    HistorialMembresiaResponse toResponse(HistorialMembresia historial);
}