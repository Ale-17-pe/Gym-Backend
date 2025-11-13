package com.gym.backend.Pago.Application.Mapper;

import com.gym.backend.Pago.Application.Dto.PagoDTO;
import com.gym.backend.Pago.Domain.Pago;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    PagoDTO toDTO(Pago pago);

    Pago toDomain(PagoDTO dto);
}
