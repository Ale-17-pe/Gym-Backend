package com.gym.backend.PaymentCode.Infrastructure.Adapter;

import com.gym.backend.PaymentCode.Domain.PaymentCode;
import com.gym.backend.PaymentCode.Domain.PaymentCodeRepositoryPort;
import com.gym.backend.PaymentCode.Domain.Enums.EstadoPaymentCode;
import com.gym.backend.PaymentCode.Infrastructure.Entity.PaymentCodeEntity;
import com.gym.backend.PaymentCode.Infrastructure.Jpa.PaymentCodeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentCodeRepositoryAdapter implements PaymentCodeRepositoryPort {
    private final PaymentCodeJpaRepository jpa;

    @Override
    public PaymentCode guardar(PaymentCode code) {
        return toDomain(jpa.save(toEntity(code)));
    }

    @Override
    public PaymentCode actualizar(PaymentCode code) {
        return jpa.findById(code.getId())
                .map(existente -> {
                    actualizarEntityDesdeDomain(existente, code);
                    return toDomain(jpa.save(existente));
                })
                .orElseThrow(() -> new RuntimeException("PaymentCode no encontrado para actualizar"));
    }

    @Override
    public Optional<PaymentCode> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<PaymentCode> buscarPorPagoId(Long pagoId) {
        return jpa.findByPagoId(pagoId).map(this::toDomain);
    }

    @Override
    public Optional<PaymentCode> buscarPorCodigo(String codigo) {
        return jpa.findByCodigo(codigo).map(this::toDomain);
    }

    @Override
    public List<PaymentCode> listarTodos() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Page<PaymentCode> listarPaginated(Pageable pageable) {
        return jpa.findAll(pageable).map(this::toDomain);
    }

    @Override
    public List<PaymentCode> listarPorEstado(EstadoPaymentCode estado) {
        return jpa.findByEstado(estado).stream().map(this::toDomain).toList();
    }

    @Override
    public List<PaymentCode> listarExpirados() {
        return jpa.findByFechaExpiracionBefore(LocalDateTime.now())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<PaymentCode> listarExpiradosNoProcesados() {
        return jpa.findByEstadoAndFechaExpiracionBefore(EstadoPaymentCode.GENERADO, LocalDateTime.now())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<PaymentCode> listarPorVencer() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime unaHoraDespues = ahora.plusHours(1);
        return jpa.findByEstadoAndFechaExpiracionBetween(
                        EstadoPaymentCode.GENERADO, ahora, unaHoraDespues)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Long contarTotal() {
        return jpa.count();
    }

    @Override
    public Long contarPorEstado(EstadoPaymentCode estado) {
        return jpa.countByEstado(estado);
    }

    @Override
    public Long contarPorVencer() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime unaHoraDespues = ahora.plusHours(1);
        return jpa.countByEstadoAndFechaExpiracionBetween(
                EstadoPaymentCode.GENERADO, ahora, unaHoraDespues);
    }

    @Override
    public int eliminarAntiguos(LocalDateTime fechaLimite) {
        return jpa.deleteByFechaCreacionBefore(fechaLimite);
    }

    private void actualizarEntityDesdeDomain(PaymentCodeEntity entity, PaymentCode domain) {
        entity.setEstado(domain.getEstado());
        entity.setFechaActualizacion(domain.getFechaActualizacion());
        entity.setFechaExpiracion(domain.getFechaExpiracion());
    }

    private PaymentCode toDomain(PaymentCodeEntity entity) {
        return PaymentCode.builder()
                .id(entity.getId())
                .pagoId(entity.getPagoId())
                .codigo(entity.getCodigo())
                .fechaGeneracion(entity.getFechaGeneracion())
                .fechaExpiracion(entity.getFechaExpiracion())
                .estado(entity.getEstado())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private PaymentCodeEntity toEntity(PaymentCode domain) {
        return PaymentCodeEntity.builder()
                .id(domain.getId())
                .pagoId(domain.getPagoId())
                .codigo(domain.getCodigo())
                .fechaGeneracion(domain.getFechaGeneracion())
                .fechaExpiracion(domain.getFechaExpiracion())
                .estado(domain.getEstado())
                .fechaCreacion(domain.getFechaCreacion())
                .fechaActualizacion(domain.getFechaActualizacion())
                .build();
    }
}