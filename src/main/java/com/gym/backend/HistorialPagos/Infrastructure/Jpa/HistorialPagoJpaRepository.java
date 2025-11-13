package com.gym.backend.HistorialPagos.Infrastructure.Jpa;


import com.gym.backend.HistorialPagos.Infrastructure.Entity.HistorialPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialPagoJpaRepository extends JpaRepository<HistorialPagoEntity, Long> {

    List<HistorialPagoEntity> findByUsuarioId(Long usuarioId);
}