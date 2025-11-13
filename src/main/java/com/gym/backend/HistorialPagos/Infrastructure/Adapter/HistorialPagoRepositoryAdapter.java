package com.gym.backend.HistorialPagos.Infrastructure.Adapter;


import com.gym.backend.HistorialPagos.Domain.HistorialPago;
import com.gym.backend.HistorialPagos.Domain.HistorialPagoRepositoryPort;
import com.gym.backend.HistorialPagos.Infrastructure.Entity.HistorialPagoEntity;
import com.gym.backend.HistorialPagos.Infrastructure.Jpa.HistorialPagoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HistorialPagoRepositoryAdapter implements HistorialPagoRepositoryPort {

    private final HistorialPagoJpaRepository jpa;

    public HistorialPagoRepositoryAdapter(HistorialPagoJpaRepository jpa) {
        this.jpa = jpa;
    }

    private HistorialPago toDomain(HistorialPagoEntity e) {
        return HistorialPago.builder()
                .id(e.getId())
                .pagoId(e.getPagoId())
                .usuarioId(e.getUsuarioId())
                .monto(e.getMonto())
                .estadoAnterior(e.getEstadoAnterior())
                .estadoNuevo(e.getEstadoNuevo())
                .fechaCambio(e.getFechaCambio())
                .build();
    }

    private HistorialPagoEntity toEntity(HistorialPago d) {
        return HistorialPagoEntity.builder()
                .id(d.getId())
                .pagoId(d.getPagoId())
                .usuarioId(d.getUsuarioId())
                .monto(d.getMonto())
                .estadoAnterior(d.getEstadoAnterior())
                .estadoNuevo(d.getEstadoNuevo())
                .fechaCambio(d.getFechaCambio())
                .build();
    }

    @Override
    public HistorialPago registrar(HistorialPago historial) {
        return toDomain(jpa.save(toEntity(historial)));
    }

    @Override
    public List<HistorialPago> listar() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<HistorialPago> listarPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioId(usuarioId).stream()
                .map(this::toDomain).toList();
    }
}