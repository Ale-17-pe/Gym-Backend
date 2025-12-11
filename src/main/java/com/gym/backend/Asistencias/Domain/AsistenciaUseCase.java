package com.gym.backend.Asistencias.Domain;

import com.gym.backend.Asistencias.Application.RachaAsistenciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AsistenciaUseCase {

    private final AsistenciaRepositoryPort repo;
    private final MembresiaValidatorPort membresiaValidator;
    private final RachaAsistenciaService rachaAsistenciaService;

    public Asistencia registrarEntrada(Long usuarioId) {
        log.info("Registrando entrada para usuario: {}", usuarioId);

        // Validar membresía activa
        var membresia = membresiaValidator.obtenerMembresiaActiva(usuarioId);
        if (membresia == null) {
            throw new IllegalStateException("El usuario no tiene una membresía activa.");
        }

        // Validar horario del gimnasio (ejemplo: 5 AM - 11 PM)
        LocalDateTime ahora = LocalDateTime.now();
        validarHorarioGimnasio(ahora);

        // Validar duplicado de entrada hoy
        LocalDateTime inicioDia = ahora.toLocalDate().atStartOfDay();
        LocalDateTime finDia = ahora.toLocalDate().atTime(23, 59, 59);

        boolean yaRegistroEntrada = repo.existeEntradaHoy(usuarioId, inicioDia, finDia);
        if (yaRegistroEntrada) {
            throw new IllegalStateException("El usuario ya registró entrada hoy.");
        }

        // Registrar entrada - NORMALIZADO 3NF (sin usuarioId directo)
        Asistencia nueva = Asistencia.builder()
                .id(null)
                .membresiaId(membresia.getId())
                .fechaHora(ahora)
                .tipo("ENTRADA")
                .estado("REGISTRADA")
                .observaciones("Entrada registrada automáticamente")
                .fechaCreacion(ahora)
                .fechaActualizacion(ahora)
                .build();

        Asistencia guardada = repo.registrar(nueva);
        log.info("Entrada registrada exitosamente para usuario: {}", usuarioId);

        // Otorgar puntos por asistencia y verificar rachas
        try {
            rachaAsistenciaService.procesarAsistencia(usuarioId, guardada.getId());
        } catch (Exception e) {
            log.warn("No se pudieron procesar puntos de asistencia: {}", e.getMessage());
        }

        return guardada;
    }

    public Asistencia registrarSalida(Long usuarioId) {
        log.info("Registrando salida para usuario: {}", usuarioId);

        LocalDateTime ahora = LocalDateTime.now();

        // Buscar entrada del día
        LocalDateTime inicioDia = ahora.toLocalDate().atStartOfDay();
        LocalDateTime finDia = ahora.toLocalDate().atTime(23, 59, 59);

        Asistencia entrada = repo.buscarEntradaHoy(usuarioId, inicioDia, finDia)
                .orElseThrow(() -> new IllegalStateException("No se encontró una entrada registrada para hoy."));

        // Validar que no haya salida ya registrada
        boolean yaRegistroSalida = repo.existeSalidaHoy(usuarioId, inicioDia, finDia);
        if (yaRegistroSalida) {
            throw new IllegalStateException("El usuario ya registró salida hoy.");
        }

        // Registrar salida - NORMALIZADO 3NF (sin usuarioId directo)
        Asistencia salida = Asistencia.builder()
                .id(null)
                .membresiaId(entrada.getMembresiaId())
                .fechaHora(ahora)
                .tipo("SALIDA")
                .estado("REGISTRADA")
                .observaciones("Salida registrada automáticamente")
                .fechaCreacion(ahora)
                .fechaActualizacion(ahora)
                .build();

        Asistencia guardada = repo.registrar(salida);
        log.info("Salida registrada exitosamente para usuario: {}", usuarioId);
        return guardada;
    }

    public Asistencia cancelarAsistencia(Long asistenciaId) {
        log.info("Cancelando asistencia ID: {}", asistenciaId);

        Asistencia asistencia = repo.buscarPorId(asistenciaId)
                .orElseThrow(() -> new IllegalStateException("Asistencia no encontrada"));

        asistencia.cancelar();
        Asistencia actualizada = repo.actualizar(asistencia);

        log.info("Asistencia cancelada: {}", asistenciaId);
        return actualizada;
    }

    @Transactional(readOnly = true)
    public List<Asistencia> listar() {
        return repo.listar();
    }

    @Transactional(readOnly = true)
    public Page<Asistencia> listarPaginated(Pageable pageable) {
        return repo.listarPaginated(pageable);
    }

    @Transactional(readOnly = true)
    public List<Asistencia> listarPorUsuario(Long usuarioId) {
        return repo.listarPorUsuario(usuarioId);
    }

    @Transactional(readOnly = true)
    public Page<Asistencia> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable) {
        return repo.listarPorUsuarioPaginated(usuarioId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Asistencia> listarPorFecha(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(23, 59, 59);
        return repo.listarPorRangoFechas(inicio, fin);
    }

    @Transactional(readOnly = true)
    public List<Asistencia> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return repo.listarPorRangoFechas(inicio, fin);
    }

    @Transactional(readOnly = true)
    public AsistenciaEstado obtenerEstadoUsuario(Long usuarioId) {
        LocalDateTime hoyInicio = LocalDate.now().atStartOfDay();
        LocalDateTime hoyFin = LocalDate.now().atTime(23, 59, 59);

        boolean tieneEntrada = repo.existeEntradaHoy(usuarioId, hoyInicio, hoyFin);
        boolean tieneSalida = repo.existeSalidaHoy(usuarioId, hoyInicio, hoyFin);
        int totalAsistenciasMes = repo.contarAsistenciasMes(usuarioId, LocalDate.now().getYear(),
                LocalDate.now().getMonthValue());

        return new AsistenciaEstado(usuarioId, tieneEntrada, tieneSalida, totalAsistenciasMes);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticas() {
        Long totalAsistencias = repo.contarTotal();
        Long asistenciasHoy = repo.contarAsistenciasHoy();
        Long usuariosActivosHoy = repo.contarUsuariosActivosHoy();
        Long promedioMensual = repo.contarPromedioMensual();

        return Map.of(
                "totalAsistencias", totalAsistencias,
                "asistenciasHoy", asistenciasHoy,
                "usuariosActivosHoy", usuariosActivosHoy,
                "promedioMensual", promedioMensual,
                "fechaConsulta", LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Map<String, Long> obtenerEstadisticasPorMes(int año, int mes) {
        Long totalEntradas = repo.contarEntradasPorMes(año, mes);
        Long totalSalidas = repo.contarSalidasPorMes(año, mes);
        Long usuariosUnicos = repo.contarUsuariosUnicosPorMes(año, mes);

        return Map.of(
                "totalEntradas", totalEntradas,
                "totalSalidas", totalSalidas,
                "usuariosUnicos", usuariosUnicos,
                "año", (long) año,
                "mes", (long) mes);
    }

    private void validarHorarioGimnasio(LocalDateTime fechaHora) {
        LocalTime hora = fechaHora.toLocalTime();
        LocalTime apertura = LocalTime.of(5, 0); // 5:00 AM
        LocalTime cierre = LocalTime.of(23, 0); // 11:00 PM

        if (hora.isBefore(apertura) || hora.isAfter(cierre)) {
            throw new IllegalStateException("El gimnasio está cerrado. Horario: " + apertura + " - " + cierre);
        }
    }

    public record AsistenciaEstado(Long usuarioId, boolean tieneEntradaHoy,
            boolean tieneSalidaHoy, int totalAsistenciasMes) {
    }
}