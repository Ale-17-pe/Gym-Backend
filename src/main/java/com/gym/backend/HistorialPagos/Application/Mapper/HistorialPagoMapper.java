package com.gym.backend.HistorialPagos.Application.Mapper;

import com.gym.backend.HistorialPagos.Application.Dto.HistorialPagoDTO;
import com.gym.backend.HistorialPagos.Domain.HistorialPago;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HistorialPagoMapper {

    HistorialPagoDTO toDTO(HistorialPago historial);

    HistorialPago toDomain(HistorialPagoDTO dto);
}