package com.gym.backend.Usuarios.Application.Mapper;


import com.gym.backend.Usuarios.Application.Dto.CrearUsuarioRequest;
import com.gym.backend.Usuarios.Application.Dto.UsuarioDTO;
import com.gym.backend.Usuarios.Application.Dto.UsuarioResponse;
import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Infrastructure.Entity.UsuarioEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UsuarioMapper {

    Usuario toDomain(UsuarioEntity entity);
    UsuarioEntity toEntity(Usuario domain);

    @Mapping(target = "password", ignore = true)
    UsuarioDTO toDTO(Usuario domain);

    Usuario toDomain(UsuarioDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", constant = "true")
    @Mapping(target = "rol", source = "rol", qualifiedByName = "stringToRol")
    @Mapping(target = "genero", source = "genero", qualifiedByName = "stringToGenero")
    Usuario toDomainFromCreateRequest(CrearUsuarioRequest request);

    @Mapping(target = "password", ignore = true)
    UsuarioResponse toResponse(Usuario domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "dni", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateDomainFromDTO(UsuarioDTO dto, @MappingTarget Usuario domain);

    @Named("stringToRol")
    default Rol stringToRol(String rol) {
        if (rol == null) return Rol.CLIENTE;
        try { return Rol.valueOf(rol.toUpperCase()); }
        catch (IllegalArgumentException e) { return Rol.CLIENTE; }
    }

    @Named("rolToString")
    default String rolToString(Rol rol) { return rol != null ? rol.name() : null; }

    @Named("stringToGenero")
    default Genero stringToGenero(String genero) {
        if (genero == null) return Genero.PREFIERO_NO_DECIR;
        try { return Genero.valueOf(genero.toUpperCase()); }
        catch (IllegalArgumentException e) { return Genero.PREFIERO_NO_DECIR; }
    }

    @Named("generoToString")
    default String generoToString(Genero genero) { return genero != null ? genero.name() : null; }
}