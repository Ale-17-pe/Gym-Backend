package com.gym.backend.Asistencias.Infrastructure.Adapter;

import com.gym.backend.Asistencias.Domain.Asistencia;
import com.gym.backend.Asistencias.Domain.AsistenciaRepositoryPort;
import com.gym.backend.Asistencias.Infrastructure.Entity.AsistenciaEntity;
import com.gym.backend.Asistencias.Infrastructure.Jpa.AsistenciaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adapter para repositorio de Asistencias - NORMALIZADO (3NF)
 * El campo usuario_id se obtiene a través de JOIN con membresias.
 */
@Component
@RequiredArgsConstructor
public class AsistenciaRepositoryAdapter implements AsistenciaRepositoryPort {

    private final AsistenciaJpaRepository jpa;

    @Override
    public Asistencia registrar(Asistencia asistencia) {
        return toDomain(jpa.save(toEntity(asistencia)));
    }

    @Override
    public Asistencia actualizar(Asistencia asistencia) {
        return jpa.findById(asistencia.getId())
                .map(existente -> {
                    actualizarEntityDesdeDomain(existente, asistencia);
                    return toDomain(jpa.save(existente));
                })
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada para actualizar"));
    }

    @Override
    public Optional<Asistencia> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Asistencia> listar() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Asistencia> listarPaginated(Pageable pageable) {
        return jpa.findAll(pageable).map(this::toDomain);
    }

    @Override
    public List<Asistencia> listarPorUsuario(Long usuarioId) {
        // NORMALIZADO 3NF: Usa JOIN con membresias
        return jpa.findByUsuarioIdViaJoin(usuarioId).stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Asistencia> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable) {
        // NORMALIZADO 3NF: Usa JOIN con membresias
        return jpa.findByUsuarioIdViaJoin(usuarioId, pageable).map(this::toDomain);
    }

    @Override
    public List<Asistencia> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return jpa.findByFechaHoraBetween(inicio, fin).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Asistencia> listarPorUsuarioYFecha(Long usuarioId, LocalDateTime inicio, LocalDateTime fin) {
        return jpa.findByUsuarioIdAndFechaHoraBetween(usuarioId, inicio, fin)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existeEntradaHoy(Long usuarioId, LocalDateTime inicio, LocalDateTime fin) {
        // NORMALIZADO 3NF: Usa JOIN con membresias
        return jpa.existsByUsuarioIdAndTipoViaJoin(usuarioId, "ENTRADA", inicio, fin);
    }

    @Override
    public boolean existeSalidaHoy(Long usuarioId, LocalDateTime inicio, LocalDateTime fin) {
        // NORMALIZADO 3NF: Usa JOIN con membresias
        return jpa.existsByUsuarioIdAndTipoViaJoin(usuarioId, "SALIDA", inicio, fin);
    }

    @Override
    public Optional<Asistencia> buscarEntradaHoy(Long usuarioId, LocalDateTime inicio, LocalDateTime fin) {
        // NORMALIZADO 3NF: Usa JOIN con membresias
        return jpa.findByUsuarioIdAndTipoViaJoin(usuarioId, "ENTRADA", inicio, fin)
                .map(this::toDomain);
    }

    @Override
    public Long contarTotal() {
        return jpa.count();
    }

    @Override
    public Long contarAsistenciasHoy() {
        LocalDateTime inicio = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime fin = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        return jpa.countByFechaHoraBetween(inicio, fin);
    }

    @Override
    public Long contarUsuariosActivosHoy() {
        LocalDateTime inicio = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime fin = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        // NORMALIZADO 3NF: Usa JOIN con membresias
        return jpa.countDistinctUsuarioIdByFechaHoraBetweenViaJoin(inicio, fin);
    }

    @Override
    public Long contarPromedioMensual() {
        var lista = jpa.obtenerConteosMensuales();
        if (lista.isEmpty())
            return 0L;

        long suma = lista.stream().mapToLong(v -> v).sum();
        return suma / lista.size();
    }

    @Override
    public Long contarEntradasPorMes(int año, int mes) {
        return jpa.countByTipoAndYearAndMonth("ENTRADA", año, mes);
    }

    @Override
    public Long contarSalidasPorMes(int año, int mes) {
        return jpa.countByTipoAndYearAndMonth("SALIDA", año, mes);
    }

    @Override
    public Long contarUsuariosUnicosPorMes(int año, int mes) {
        // NORMALIZADO 3NF: Usa JOIN con membresias
        return jpa.countDistinctUsuarioIdByYearAndMonthViaJoin(año, mes);
    }

    @Override
    public int contarAsistenciasMes(Long usuarioId, int año, int mes) {
        // NORMALIZADO 3NF: Usa JOIN con membresias
        return jpa.countByUsuarioIdAndYearAndMonthViaJoin(usuarioId, año, mes);
    }

    private void actualizarEntityDesdeDomain(AsistenciaEntity entity, Asistencia domain) {
        entity.setEstado(domain.getEstado());
        entity.setObservaciones(domain.getObservaciones());
        entity.setFechaActualizacion(domain.getFechaActualizacion());
    }

    /**
     * Convierte Entity a Domain - NORMALIZADO 3NF
     * (sin usuarioId directo, se obtiene vía membresía)
     */
    private Asistencia toDomain(AsistenciaEntity entity) {
        return Asistencia.builder()
                .id(entity.getId())
                .membresiaId(entity.getMembresiaId())
                .fechaHora(entity.getFechaHora())
                .tipo(entity.getTipo())
                .estado(entity.getEstado())
                .observaciones(entity.getObservaciones())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    /**
     * Convierte Domain a Entity - NORMALIZADO 3NF
     * (sin usuarioId directo)
     */
    private AsistenciaEntity toEntity(Asistencia domain) {
        return AsistenciaEntity.builder()
                .id(domain.getId())
                .membresiaId(domain.getMembresiaId())
                .fechaHora(domain.getFechaHora())
                .tipo(domain.getTipo())
                .estado(domain.getEstado())
                .observaciones(domain.getObservaciones())
                .fechaCreacion(domain.getFechaCreacion())
                .fechaActualizacion(domain.getFechaActualizacion())
                .build();
    }
}