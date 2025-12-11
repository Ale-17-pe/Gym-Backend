package com.gym.backend.Usuarios.Infrastructure.Jpa;

import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Infrastructure.Entity.UsuarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    List<UsuarioEntity> findByActivo(Boolean activo);

    // Buscar usuarios que tengan un rol específico
    @Query("SELECT u FROM UsuarioEntity u JOIN u.roles r WHERE r = :rol")
    List<UsuarioEntity> findByRol(@Param("rol") Rol rol);

    @Query("SELECT u FROM UsuarioEntity u JOIN u.roles r WHERE r = :rol")
    Page<UsuarioEntity> findByRolPaginated(@Param("rol") Rol rol, Pageable pageable);

    // Contar usuarios por rol
    @Query("SELECT COUNT(u) FROM UsuarioEntity u JOIN u.roles r WHERE r = :rol")
    long countByRol(@Param("rol") Rol rol);
}