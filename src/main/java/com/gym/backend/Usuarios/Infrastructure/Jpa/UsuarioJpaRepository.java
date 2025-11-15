package com.gym.backend.Usuarios.Infrastructure.Jpa;

import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Infrastructure.Entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByEmail(String email);
    Optional<UsuarioEntity> findByDni(String dni);

    List<UsuarioEntity> findByGenero(Genero genero);
    List<UsuarioEntity> findByActivo(Boolean activo);
    List<UsuarioEntity> findByRol(Rol rol);
    List<UsuarioEntity> findByActivoTrue();
    List<UsuarioEntity> findByActivoFalse();
}