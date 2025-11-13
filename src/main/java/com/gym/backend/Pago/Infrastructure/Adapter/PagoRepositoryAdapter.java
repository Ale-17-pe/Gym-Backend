package com.gym.backend.Pago.Infrastructure.Adapter;


import com.gym.backend.Pago.Domain.Pago;
import com.gym.backend.Pago.Domain.PagoRepositoryPort;
import com.gym.backend.Pago.Infrastructure.Entity.PagoEntity;
import com.gym.backend.Pago.Infrastructure.Jpa.PagoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagoRepositoryAdapter implements PagoRepositoryPort {

    private final PagoJpaRepository jpa;

    public PagoRepositoryAdapter(PagoJpaRepository jpa) {
        this.jpa = jpa;
    }

    private Pago toDomain(PagoEntity e) {
        return Pago.builder()
                .id(e.getId())
                .usuarioId(e.getUsuarioId())
                .membresiaId(e.getMembresiaId())
                .monto(e.getMonto())
                .estado(e.getEstado())
                .fechaPago(e.getFechaPago())
                .build();
    }

    private PagoEntity toEntity(Pago d) {
        return PagoEntity.builder()
                .id(d.getId())
                .usuarioId(d.getUsuarioId())
                .membresiaId(d.getMembresiaId())
                .monto(d.getMonto())
                .estado(d.getEstado())
                .fechaPago(d.getFechaPago())
                .build();
    }

    @Override
    public Pago registrar(Pago pago) {
        return toDomain(jpa.save(toEntity(pago)));
    }

    @Override
    public Pago actualizar(Long id, Pago pago) {
        var e = jpa.findById(id)
                .orElseThrow(() -> new IllegalStateException("Pago no encontrado"));

        e.setEstado(pago.getEstado());

        return toDomain(jpa.save(e));
    }

    @Override
    public Pago obtenerPorId(Long id) {
        return jpa.findById(id).map(this::toDomain)
                .orElseThrow(() -> new IllegalStateException("Pago no existe"));
    }

    @Override
    public List<Pago> listar() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Pago> listarPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioId(usuarioId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Pago> listarPorMembresia(Long membresiaId) {
        return jpa.findByMembresiaId(membresiaId).stream().map(this::toDomain).toList();
    }
}
