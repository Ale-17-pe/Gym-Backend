package com.gym.backend.Pago.Infrastructure.Adapter;

import com.gym.backend.Pago.Domain.Enum.EstadoPago;
import com.gym.backend.Pago.Domain.Pago;
import com.gym.backend.Pago.Domain.PagoRepositoryPort;
import com.gym.backend.Pago.Infrastructure.Entity.PagoEntity;
import com.gym.backend.Pago.Infrastructure.Jpa.PagoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PagoRepositoryAdapter implements PagoRepositoryPort {
    private final PagoJpaRepository jpa;

    @Override public Pago guardar(Pago pago) { return toDomain(jpa.save(toEntity(pago))); }

    @Override public Pago actualizar(Pago pago) {
        PagoEntity existente = jpa.findById(pago.getId()).orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        existente.setEstado(pago.getEstado());
        existente.setFechaPago(pago.getFechaPago());
        existente.setReferencia(pago.getReferencia());
        return toDomain(jpa.save(existente));
    }

    @Override public Optional<Pago> buscarPorId(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public List<Pago> listar() { return jpa.findAll().stream().map(this::toDomain).toList(); }
    @Override public List<Pago> listarPorUsuario(Long usuarioId) { return jpa.findByUsuarioId(usuarioId).stream().map(this::toDomain).toList(); }
    @Override public List<Pago> listarPorEstado(EstadoPago estado) { return jpa.findByEstado(estado).stream().map(this::toDomain).toList(); }
    @Override public List<Pago> listarPagosPendientes() { return jpa.findPagosPendientes().stream().map(this::toDomain).toList(); }

    private Pago toDomain(PagoEntity entity) {
        return Pago.builder()
                .id(entity.getId()).usuarioId(entity.getUsuarioId()).planId(entity.getPlanId())
                .monto(entity.getMonto()).estado(entity.getEstado()).metodoPago(entity.getMetodoPago())
                .referencia(entity.getReferencia()).fechaCreacion(entity.getFechaCreacion())
                .fechaPago(entity.getFechaPago()).fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private PagoEntity toEntity(Pago domain) {
        return PagoEntity.builder()
                .id(domain.getId()).usuarioId(domain.getUsuarioId()).planId(domain.getPlanId())
                .monto(domain.getMonto()).estado(domain.getEstado()).metodoPago(domain.getMetodoPago())
                .referencia(domain.getReferencia()).fechaCreacion(domain.getFechaCreacion())
                .fechaPago(domain.getFechaPago()).fechaActualizacion(domain.getFechaActualizacion())
                .build();
    }
}
