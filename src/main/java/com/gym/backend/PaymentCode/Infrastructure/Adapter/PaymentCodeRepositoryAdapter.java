package com.gym.backend.PaymentCode.Infrastructure.Adapter;

import com.gym.backend.PaymentCode.Domain.PaymentCode;
import com.gym.backend.PaymentCode.Domain.PaymentCodeRepositoryPort;
import com.gym.backend.PaymentCode.Infrastructure.Entity.PaymentCodeEntity;
import com.gym.backend.PaymentCode.Infrastructure.Jpa.PaymentCodeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentCodeRepositoryAdapter implements PaymentCodeRepositoryPort {
    private final PaymentCodeJpaRepository jpa;

    @Override public PaymentCode guardar(PaymentCode code) { return toDomain(jpa.save(toEntity(code))); }
    @Override public Optional<PaymentCode> buscarPorId(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public Optional<PaymentCode> buscarPorPagoId(Long pagoId) { return jpa.findByPagoId(pagoId).map(this::toDomain); }
    @Override public Optional<PaymentCode> buscarPorCodigo(String codigo) { return jpa.findByCodigo(codigo).map(this::toDomain); }
    @Override public PaymentCode actualizar(PaymentCode code) { return toDomain(jpa.save(toEntity(code))); }

    private PaymentCode toDomain(PaymentCodeEntity entity) {
        return PaymentCode.builder()
                .id(entity.getId()).pagoId(entity.getPagoId()).codigo(entity.getCodigo())
                .fechaGeneracion(entity.getFechaGeneracion()).fechaExpiracion(entity.getFechaExpiracion())
                .estado(entity.getEstado()).build();
    }

    private PaymentCodeEntity toEntity(PaymentCode domain) {
        return PaymentCodeEntity.builder()
                .id(domain.getId()).pagoId(domain.getPagoId()).codigo(domain.getCodigo())
                .fechaGeneracion(domain.getFechaGeneracion()).fechaExpiracion(domain.getFechaExpiracion())
                .estado(domain.getEstado()).build();
    }
}