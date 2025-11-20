package com.gym.backend.Membresias.Domain;

import com.gym.backend.HistorialMembresias.Domain.HistorialMembresiaUseCase;
import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import com.gym.backend.Membresias.Domain.Exceptions.MembresiaNotFoundException;
import com.gym.backend.Membresias.Domain.Exceptions.MembresiaValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MembresiaUseCase {
    private final MembresiaRepositoryPort repo;
    private final HistorialMembresiaUseCase historialMembresiaUseCase;


    public Membresia crear(Membresia membresia) {
        log.info("Creando nueva membresía - Usuario: {}, Plan: {}",
                membresia.getUsuarioId(), membresia.getPlanId());
        membresia.validar();

        if (repo.buscarActivaPorUsuario(membresia.getUsuarioId()).isPresent()) {
            throw new MembresiaValidationException("El usuario ya tiene una membresía activa");
        }

        Membresia nueva = Membresia.builder()
                .usuarioId(membresia.getUsuarioId())
                .planId(membresia.getPlanId())
                .pagoId(membresia.getPagoId())
                .fechaInicio(membresia.getFechaInicio())
                .fechaFin(membresia.getFechaFin())
                .estado(EstadoMembresia.ACTIVA)
                .build();

        Membresia guardada = repo.guardar(nueva);

        // 🔥 Historial
        historialMembresiaUseCase.registrarCambioAutomatico(
                guardada.getId(),
                guardada.getUsuarioId(),
                guardada.getPlanId(),
                "CREAR",
                null,
                "ACTIVA",
                "Creación de membresía por pago"
        );

        return guardada;
    }

    public Membresia extender(Long membresiaId, Integer diasExtension) {

        Membresia m = obtener(membresiaId);

        LocalDate fechaAnterior = m.getFechaFin();
        m.setFechaFin(m.getFechaFin().plusDays(diasExtension));

        Membresia actualizada = repo.actualizar(m);

        historialMembresiaUseCase.registrarCambioAutomatico(
                m.getId(),
                m.getUsuarioId(),
                m.getPlanId(),
                "EXTENDER",
                fechaAnterior.toString(),
                m.getFechaFin().toString(),
                "Extensión automática por nuevo pago"
        );

        return actualizada;
    }

    public Membresia suspender(Long membresiaId) {
        log.info("Suspender membresía ID: {}", membresiaId);

        Membresia membresia = obtener(membresiaId);
        membresia.suspender();

        return repo.actualizar(membresia);
    }

    public Membresia reactivar(Long membresiaId) {
        log.info("Reactivar membresía ID: {}", membresiaId);

        Membresia membresia = obtener(membresiaId);

        if (membresia.estaVencida()) {
            throw new MembresiaValidationException("No se puede reactivar una membresía vencida");
        }

        membresia.activar();
        return repo.actualizar(membresia);
    }

    public Membresia cancelar(Long membresiaId) {
        log.info("Cancelar membresía ID: {}", membresiaId);

        Membresia membresia = obtener(membresiaId);
        membresia.cancelar();

        return repo.actualizar(membresia);
    }

    @Transactional(readOnly = true)
    public Membresia obtener(Long id) {
        return repo.buscarPorId(id).orElseThrow(() -> new MembresiaNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Membresia> listar() {
        return repo.listar();
    }

    @Transactional(readOnly = true)
    public Page<Membresia> listarPaginated(Pageable pageable) {
        return repo.listarPaginated(pageable);
    }

    @Transactional(readOnly = true)
    public List<Membresia> listarPorUsuario(Long usuarioId) {
        return repo.listarPorUsuario(usuarioId);
    }

    public Page<Membresia> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable) {
        return repo.listarPorUsuarioPaginated(usuarioId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Membresia> listarActivas() {
        return repo.listarPorEstado(EstadoMembresia.ACTIVA);
    }

    @Transactional(readOnly = true)
    public Page<Membresia> listarActivasPaginated(Pageable pageable) {
        return repo.listarPorEstadoPaginated(EstadoMembresia.ACTIVA, pageable);
    }

    @Transactional(readOnly = true)
    public List<Membresia> listarPorVencer() {
        return repo.listarPorVencer();
    }

    @Transactional(readOnly = true)
    public List<Membresia> listarVencidas() {
        return repo.listarPorEstado(EstadoMembresia.VENCIDA);
    }

    @Transactional(readOnly = true)
    public Membresia obtenerActivaPorUsuario(Long usuarioId) {
        return repo.buscarActivaPorUsuario(usuarioId).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean verificarAcceso(Long usuarioId) {
        Membresia membresia = obtenerActivaPorUsuario(usuarioId);
        return membresia != null && membresia.estaActiva();
    }

    // Tarea programada para vencer membresías automáticamente
    @Transactional
    public void vencerMembresiasAutomaticamente() {
        log.info("Ejecutando tarea programada para vencer membresías...");

        List<Membresia> membresiasPorVencer = repo.listarPorVencer();
        int contador = 0;

        for (Membresia membresia : membresiasPorVencer) {
            if (membresia.estaVencida()) {
                membresia.vencer();
                repo.actualizar(membresia);
                contador++;
                log.info("Membresía ID: {} vencida automáticamente", membresia.getId());
            }
        }

        log.info("Tarea completada: {} membresías vencidas", contador);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticas() {
        Long totalMembresias = repo.contarTotal();
        Long membresiasActivas = repo.contarPorEstado(EstadoMembresia.ACTIVA);
        Long membresiasPorVencer = repo.contarPorVencer();
        Long membresiasVencidas = repo.contarPorEstado(EstadoMembresia.VENCIDA);

        return Map.of(
                "totalMembresias", totalMembresias,
                "membresiasActivas", membresiasActivas,
                "membresiasPorVencer", membresiasPorVencer,
                "membresiasVencidas", membresiasVencidas,
                "tasaActivas", totalMembresias > 0 ? (double) membresiasActivas / totalMembresias * 100 : 0
        );
    }

    @Transactional(readOnly = true)
    public List<Membresia> buscarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return repo.buscarPorRangoFechas(fechaInicio, fechaFin);
    }
}