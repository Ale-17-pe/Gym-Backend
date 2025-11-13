package com.gym.backend.Membresias.Application.Mapper;

import com.gym.backend.Membresias.Application.Dto.MembresiaDTO;
import com.gym.backend.Membresias.Domain.Membresia;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MembresiaMapper {

    MembresiaDTO toDTO(Membresia membresia);

    Membresia toDomain(MembresiaDTO dto);
}
