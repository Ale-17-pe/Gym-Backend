package com.gym.backend.PaymentCode.Infrastructure.Jpa;

import com.gym.backend.PaymentCode.Infrastructure.Entity.PaymentCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentCodeJpaRepository extends JpaRepository<PaymentCodeEntity, Long> {

    Optional<PaymentCodeEntity> findByPagoId(Long pagoId);

    Optional<PaymentCodeEntity> findByCodigo(String codigo);
}
