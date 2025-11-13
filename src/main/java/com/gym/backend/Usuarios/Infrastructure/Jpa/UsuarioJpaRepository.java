package com.gym.backend.Usuarios.Infrastructure.Jpa;

import com.gym.backend.Usuarios.Infrastructure.Entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    UsuarioEntity findByEmail(String email);

    UsuarioEntity findByDni(String dni);
}