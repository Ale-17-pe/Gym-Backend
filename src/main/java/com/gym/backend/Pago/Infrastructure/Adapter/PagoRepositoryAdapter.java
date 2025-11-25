package com.gym.backend.Pago.Infrastructure.Adapter;

import com.gym.backend.Pago.Domain.Enum.EstadoPago;
import com.gym.backend.Pago.Domain.Pago;
import com.gym.backend.Pago.Domain.PagoRepositoryPort;
import com.gym.backend.Pago.Infrastructure.Entity.PagoEntity;
import com.gym.backend.Pago.Infrastructure.Jpa.PagoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PagoRepositoryAdapter implements PagoRepositoryPort {
    private final PagoJpaRepository jpa;

    @Override
    public Pago guardar(Pago pago) {
        return toDomain(jpa.save(toEntity(pago)));
    }

    @Override
    public Pago actualizar(Pago pago) {
        return jpa.findById(pago.getId())
                .map(existente -> {
                    actualizarEntityDesdeDomain(existente, pago);
                    return toDomain(jpa.save(existente));
                })
                .orElseThrow(() -> new RuntimeException("Pago no encontrado para actualizar"));
    }

    @Override
    public Optional<Pago> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Pago> buscarPorReferencia(String referencia) {
        return jpa.findByReferencia(referencia).map(this::toDomain);
    }

    @Override
    public List<Pago> listar() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Pago> listarPaginated(Pageable pageable) {
        return jpa.findAll(pageable).map(this::toDomain);
    }

    @Override
    public List<Pago> listarPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioId(usuarioId).stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Pago> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable) {
        return jpa.findByUsuarioId(usuarioId, pageable).map(this::toDomain);
    }

    @Override
    public List<Pago> listarPorEstado(EstadoPago estado) {
        return jpa.findByEstado(estado).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Pago> listarPagosPendientes() {
        return jpa.findPagosPendientes().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Pago> listarPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return jpa.findByFechaCreacionBetween(fechaInicio, fechaFin)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Double obtenerIngresosTotalesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return jpa.sumMontoByEstadoAndFechaCreacionBetween(
                EstadoPago.CONFIRMADO, fechaInicio, fechaFin);
    }

    @Override
    public Long contarPagosPorEstadoYFecha(EstadoPago estado, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return jpa.countByEstadoAndFechaCreacionBetween(estado, fechaInicio, fechaFin);
    }

    private void actualizarEntityDesdeDomain(PagoEntity entity, Pago domain) {
        entity.setEstado(domain.getEstado());
        entity.setFechaPago(domain.getFechaPago());
        entity.setReferencia(domain.getReferencia());
        entity.setCodigoPago(domain.getCodigoPago());
        entity.setFechaActualizacion(domain.getFechaActualizacion());
    }

    private Pago toDomain(PagoEntity entity) {
        return Pago.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId())
                .planId(entity.getPlanId())
                .monto(entity.getMonto())
                .estado(entity.getEstado())
                .metodoPago(entity.getMetodoPago())
                .referencia(entity.getReferencia())
                .codigoPago(entity.getCodigoPago())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaPago(entity.getFechaPago())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private PagoEntity toEntity(Pago domain) {
        return PagoEntity.builder()
                .id(domain.getId())
                .usuarioId(domain.getUsuarioId())
                .planId(domain.getPlanId())
                .monto(domain.getMonto())
                .estado(domain.getEstado())
                .metodoPago(domain.getMetodoPago())
                .referencia(domain.getReferencia())
                .codigoPago(domain.getCodigoPago())
                .fechaCreacion(domain.getFechaCreacion())
                .fechaPago(domain.getFechaPago())
                .fechaActualizacion(domain.getFechaActualizacion())
                .build();
    }
}