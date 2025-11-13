package com.gym.backend.Usuarios.Application.Mapper;

import com.gym.backend.Usuarios.Application.Dto.UsuarioDTO;
import com.gym.backend.Usuarios.Domain.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    UsuarioDTO toDTO(Usuario usuario);

    Usuario toDomain(UsuarioDTO dto);
}