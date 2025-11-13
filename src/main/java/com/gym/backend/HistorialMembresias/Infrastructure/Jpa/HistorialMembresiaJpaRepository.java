package com.gym.backend.HistorialMembresias.Infrastructure.Jpa;

import com.gym.backend.HistorialMembresias.Infrastructure.Entity.HistorialMembresiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialMembresiaJpaRepository extends JpaRepository<HistorialMembresiaEntity, Long> {

    List<HistorialMembresiaEntity> findByUsuarioId(Long usuarioId);
}
