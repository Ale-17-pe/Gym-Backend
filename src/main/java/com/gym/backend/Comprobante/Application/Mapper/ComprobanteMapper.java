package com.gym.backend.Comprobante.Application.Mapper;

import com.gym.backend.Comprobante.Application.Dto.ComprobanteResponse;
import com.gym.backend.Comprobante.Domain.Comprobante;
import com.gym.backend.Comprobante.Infrastructure.Entity.ComprobanteEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ComprobanteMapper {
    Comprobante toDomain(ComprobanteEntity entity);

    ComprobanteEntity toEntity(Comprobante domain);

    ComprobanteResponse toResponse(Comprobante domain);
}
