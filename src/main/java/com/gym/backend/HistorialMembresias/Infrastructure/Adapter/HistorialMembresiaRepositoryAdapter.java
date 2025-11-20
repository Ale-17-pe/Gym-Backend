package com.gym.backend.HistorialMembresias.Infrastructure.Adapter;

import com.gym.backend.HistorialMembresias.Domain.HistorialMembresia;
import com.gym.backend.HistorialMembresias.Domain.HistorialMembresiaRepositoryPort;
import com.gym.backend.HistorialMembresias.Infrastructure.Entity.HistorialMembresiaEntity;
import com.gym.backend.HistorialMembresias.Infrastructure.Jpa.HistorialMembresiaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HistorialMembresiaRepositoryAdapter implements HistorialMembresiaRepositoryPort {

    private final HistorialMembresiaJpaRepository jpa;

    @Override
    public HistorialMembresia registrar(HistorialMembresia historial) {
        return toDomain(jpa.save(toEntity(historial)));
    }

    @Override
    public List<HistorialMembresia> listar() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Page<HistorialMembresia> listarPaginated(Pageable pageable) {
        return jpa.findAll(pageable).map(this::toDomain);
    }

    @Override
    public List<HistorialMembresia> listarPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioId(usuarioId).stream().map(this::toDomain).toList();
    }

    @Override
    public Page<HistorialMembresia> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable) {
        return jpa.findByUsuarioId(usuarioId, pageable).map(this::toDomain);
    }

    @Override
    public List<HistorialMembresia> listarPorMembresia(Long membresiaId) {
        return jpa.findByMembresiaId(membresiaId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<HistorialMembresia> listarPorAccion(String accion) {
        return jpa.findByAccion(accion).stream().map(this::toDomain).toList();
    }

    @Override
    public List<HistorialMembresia> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return jpa.findByFechaCambioBetween(inicio, fin).stream().map(this::toDomain).toList();
    }

    @Override
    public HistorialMembresia obtenerUltimoCambio(Long membresiaId) {
        return jpa.findTopByMembresiaIdOrderByFechaCambioDesc(membresiaId)
                .map(this::toDomain)
                .orElse(null);
    }

    @Override
    public List<HistorialMembresia> obtenerCambiosRecientes(int limite) {
        return jpa.findTopNByOrderByFechaCambioDesc(limite).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Long contarTotal() {
        return jpa.count();
    }

    @Override
    public Long contarCambiosHoy() {
        LocalDateTime inicio = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime fin = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        return jpa.countByFechaCambioBetween(inicio, fin);
    }

    @Override
    public Long contarPorAccion(String accion) {
        return jpa.countByAccion(accion);
    }

    @Override
    public Long contarCambiosPorMes(int año, int mes) {
        return jpa.countByYearAndMonth(año, mes);
    }

    @Override
    public Long contarCreacionesPorMes(int año, int mes) {
        return jpa.countByAccionAndYearAndMonth("CREAR", año, mes);
    }

    @Override
    public Long contarExtensionesPorMes(int año, int mes) {
        return jpa.countByAccionAndYearAndMonth("EXTENDER", año, mes);
    }

    private HistorialMembresia toDomain(HistorialMembresiaEntity entity) {
        return HistorialMembresia.builder()
                .id(entity.getId())
                .membresiaId(entity.getMembresiaId())
                .usuarioId(entity.getUsuarioId())
                .planId(entity.getPlanId())
                .accion(entity.getAccion())
                .estadoAnterior(entity.getEstadoAnterior())
                .estadoNuevo(entity.getEstadoNuevo())
                .motivoCambio(entity.getMotivoCambio())
                .usuarioModificacion(entity.getUsuarioModificacion())
                .ipOrigen(entity.getIpOrigen())
                .fechaCambio(entity.getFechaCambio())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private HistorialMembresiaEntity toEntity(HistorialMembresia domain) {
        return HistorialMembresiaEntity.builder()
                .id(domain.getId())
                .membresiaId(domain.getMembresiaId())
                .usuarioId(domain.getUsuarioId())
                .planId(domain.getPlanId())
                .accion(domain.getAccion())
                .estadoAnterior(domain.getEstadoAnterior())
                .estadoNuevo(domain.getEstadoNuevo())
                .motivoCambio(domain.getMotivoCambio())
                .usuarioModificacion(domain.getUsuarioModificacion())
                .ipOrigen(domain.getIpOrigen())
                .fechaCambio(domain.getFechaCambio())
                .fechaCreacion(domain.getFechaCreacion())
                .fechaActualizacion(domain.getFechaActualizacion())
                .build();
    }
}