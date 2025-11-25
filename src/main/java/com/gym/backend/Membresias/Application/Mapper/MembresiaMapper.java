package com.gym.backend.Membresias.Application.Mapper;

import com.gym.backend.Membresias.Application.Dto.CrearMembresiaRequest;
import com.gym.backend.Membresias.Application.Dto.MembresiaDTO;
import com.gym.backend.Membresias.Application.Dto.MembresiaResponse;
import com.gym.backend.Membresias.Domain.Membresia;
import com.gym.backend.Membresias.Infrastructure.Entity.MembresiaEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MembresiaMapper {
    Membresia toDomain(MembresiaEntity entity);

    MembresiaEntity toEntity(Membresia domain);

    @Mapping(target = "qrBase64", ignore = true)
    MembresiaDTO toDTO(Membresia domain);

    Membresia toDomain(MembresiaDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "codigoAcceso", ignore = true)
    @Mapping(target = "codigoExpiracion", ignore = true)
    Membresia toDomainFromRequest(CrearMembresiaRequest request);

    @Mapping(target = "activa", expression = "java(membresia.estaActiva())")
    @Mapping(target = "diasRestantes", expression = "java(membresia.diasRestantes())")
    @Mapping(target = "nombrePlan", ignore = true)
    @Mapping(target = "nombreUsuario", ignore = true)
    MembresiaResponse toResponse(Membresia membresia);
}