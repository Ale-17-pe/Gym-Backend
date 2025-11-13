package com.gym.backend.Membresias.Infrastructure.Jpa;

import com.gym.backend.Membresias.Infrastructure.Entity.MembresiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembresiaJpaRepository extends JpaRepository<MembresiaEntity, Long> {

    List<MembresiaEntity> findByUsuarioId(Long usuarioId);

    MembresiaEntity findByUsuarioIdAndEstado(Long usuarioId, String estado);
}
