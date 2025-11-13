package com.gym.backend.Pago.Infrastructure.Jpa;

import com.gym.backend.Pago.Infrastructure.Entity.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoJpaRepository extends JpaRepository<PagoEntity, Long> {

    List<PagoEntity> findByUsuarioId(Long usuarioId);

    List<PagoEntity> findByMembresiaId(Long membresiaId);
}
