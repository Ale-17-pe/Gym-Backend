package com.gym.backend.Pago.Application.Mapper;

import com.gym.backend.Pago.Application.Dto.CrearPagoRequest;
import com.gym.backend.Pago.Application.Dto.PagoDTO;
import com.gym.backend.Pago.Application.Dto.PagoResponse;
import com.gym.backend.Pago.Domain.Pago;
import com.gym.backend.Pago.Infrastructure.Entity.PagoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PagoMapper {
    Pago toDomain(PagoEntity entity);
    PagoEntity toEntity(Pago domain);
    PagoDTO toDTO(Pago domain);
    Pago toDomain(PagoDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", constant = "PENDIENTE")
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "fechaPago", ignore = true)
    @Mapping(target = "fechaActualizacion", expression = "java(java.time.LocalDateTime.now())")
    Pago toDomainFromCreateRequest(CrearPagoRequest request);

    @Mapping(target = "codigoPago", ignore = true)
    PagoResponse toResponse(Pago domain);
}