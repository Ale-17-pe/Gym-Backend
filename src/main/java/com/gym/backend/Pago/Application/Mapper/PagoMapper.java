package com.gym.backend.Pago.Application.Mapper;

import com.gym.backend.Pago.Application.Dto.CrearPagoRequest;
import com.gym.backend.Pago.Application.Dto.PagoDTO;
import com.gym.backend.Pago.Application.Dto.PagoResponse;
import com.gym.backend.Pago.Domain.Pago;
import com.gym.backend.Pago.Infrastructure.Entity.PagoEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PagoMapper {
    Pago toDomain(PagoEntity entity);

    // -----------------------------
    // DOMAIN → ENTITY
    // (Se usa en el adapter JPA)
    // -----------------------------
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "fechaCreacion", ignore = true),
            @Mapping(target = "fechaActualizacion", ignore = true),
            @Mapping(target = "fechaPago", ignore = true)
    })
    PagoEntity toEntity(Pago domain);

    // -----------------------------
    // DOMAIN → DTO
    // -----------------------------
    @Mapping(target = "fechaPago", source = "fechaPago")
    PagoDTO toDTO(Pago domain);

    // -----------------------------
    // REQUEST → DOMAIN
    // El UseCase rellena estado y fechas.
    // -----------------------------
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "estado", ignore = true),
            @Mapping(target = "fechaCreacion", ignore = true),
            @Mapping(target = "fechaPago", ignore = true),
            @Mapping(target = "fechaActualizacion", ignore = true)
    })
    Pago toDomainFromCreateRequest(CrearPagoRequest request);

    // -----------------------------
    // DOMAIN → RESPONSE
    // codigoPago se setea en el controller
    // -----------------------------
    @Mapping(target = "codigoPago", ignore = true)
    PagoResponse toResponse(Pago domain);
}