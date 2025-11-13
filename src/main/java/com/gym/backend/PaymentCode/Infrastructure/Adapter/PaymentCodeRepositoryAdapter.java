package com.gym.backend.PaymentCode.Infrastructure.Adapter;

import com.gym.backend.PaymentCode.Domain.PaymentCode;
import com.gym.backend.PaymentCode.Domain.PaymentCodeRepositoryPort;
import com.gym.backend.PaymentCode.Infrastructure.Entity.PaymentCodeEntity;
import com.gym.backend.PaymentCode.Infrastructure.Jpa.PaymentCodeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaymentCodeRepositoryAdapter implements PaymentCodeRepositoryPort {

    private final PaymentCodeJpaRepository jpa;

    public PaymentCodeRepositoryAdapter(PaymentCodeJpaRepository jpa) {
        this.jpa = jpa;
    }

    private PaymentCode toDomain(PaymentCodeEntity e) {
        return PaymentCode.builder()
                .id(e.getId())
                .pagoId(e.getPagoId())
                .codigo(e.getCodigo())
                .fechaGeneracion(e.getFechaGeneracion())
                .fechaExpiracion(e.getFechaExpiracion())
                .estado(e.getEstado())
                .build();
    }

    private PaymentCodeEntity toEntity(PaymentCode d) {
        return PaymentCodeEntity.builder()
                .id(d.getId())
                .pagoId(d.getPagoId())
                .codigo(d.getCodigo())
                .fechaGeneracion(d.getFechaGeneracion())
                .fechaExpiracion(d.getFechaExpiracion())
                .estado(d.getEstado())
                .build();
    }

    @Override
    public PaymentCode guardar(PaymentCode code) {
        return toDomain(jpa.save(toEntity(code)));
    }

    @Override
    public Optional<PaymentCode> buscarPorPago(Long pagoId) {
        return jpa.findByPagoId(pagoId).map(this::toDomain);
    }

    @Override
    public Optional<PaymentCode> buscarPorCodigo(String codigo) {
        return jpa.findByCodigo(codigo).map(this::toDomain);
    }

    @Override
    public void actualizarEstado(Long id, String estado) {
        PaymentCodeEntity e = jpa.findById(id).orElseThrow();
        e.setEstado(estado);
        jpa.save(e);
    }
}
