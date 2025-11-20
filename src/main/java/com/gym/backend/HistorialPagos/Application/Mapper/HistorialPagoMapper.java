package com.gym.backend.HistorialPagos.Application.Mapper;

import com.gym.backend.HistorialPagos.Application.Dto.HistorialPagoDTO;
import com.gym.backend.HistorialPagos.Application.Dto.HistorialPagoResponse;
import com.gym.backend.HistorialPagos.Domain.HistorialPago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistorialPagoMapper {

    HistorialPagoDTO toDTO(HistorialPago historial);
    HistorialPago toDomain(HistorialPagoDTO dto);

    @Mapping(target = "fecha", source = "fechaCambio", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "hora", source = "fechaCambio", dateFormat = "HH:mm:ss")
    @Mapping(target = "tipoCambio", expression = "java(historial.obtenerTipoCambio())")
    @Mapping(target = "mensaje", ignore = true)
    HistorialPagoResponse toResponse(HistorialPago historial);
}