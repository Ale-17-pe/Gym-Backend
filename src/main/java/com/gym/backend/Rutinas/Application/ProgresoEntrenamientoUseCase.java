package com.gym.backend.Rutinas.Application;

import com.gym.backend.Rutinas.Domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Caso de uso para registrar y ver el progreso de entrenamientos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProgresoEntrenamientoUseCase {

    private final RegistroEntrenamientoRepositoryPort registroRepository;
    private final RutinaRepositoryPort rutinaRepository;
    private final DiaRutinaRepositoryPort diaRutinaRepository;
    private final EjercicioRutinaRepositoryPort ejercicioRutinaRepository;
    private final EjercicioRepositoryPort ejercicioRepository;

    /**
     * Iniciar un nuevo entrenamiento
     */
    @Transactional
    public RegistroEntrenamiento iniciarEntrenamiento(Long usuarioId, Long diaRutinaId) {
        log.info("Iniciando entrenamiento para usuario: {}", usuarioId);

        DiaRutina dia = diaRutinaRepository.buscarPorId(diaRutinaId)
                .orElseThrow(() -> new IllegalArgumentException("Día de rutina no encontrado"));

        Rutina rutina = rutinaRepository.buscarPorId(dia.getRutinaId())
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));

        if (!rutina.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para esta rutina");
        }

        RegistroEntrenamiento registro = RegistroEntrenamiento.builder()
                .usuarioId(usuarioId)
                .rutinaId(rutina.getId())
                .diaRutinaId(diaRutinaId)
                .fechaEntrenamiento(LocalDateTime.now())
                .completado(false)
                .build();

        RegistroEntrenamiento guardado = registroRepository.guardar(registro);
        log.info("✅ Entrenamiento iniciado con ID: {}", guardado.getId());
        return guardado;
    }

    /**
     * Finalizar un entrenamiento
     */
    @Transactional
    public RegistroEntrenamiento finalizarEntrenamiento(Long registroId, Long usuarioId,
            FinalizarEntrenamientoRequest request) {
        RegistroEntrenamiento registro = registroRepository.buscarPorId(registroId)
                .orElseThrow(() -> new IllegalArgumentException("Registro no encontrado"));

        if (!registro.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para este registro");
        }

        registro.setDuracionMinutos(request.duracionMinutos());
        registro.setNotas(request.notas());
        registro.setNivelEnergia(request.nivelEnergia());
        registro.setNivelSatisfaccion(request.nivelSatisfaccion());
        registro.setCompletado(true);

        log.info("✅ Entrenamiento finalizado: {} minutos", request.duracionMinutos());
        return registroRepository.guardar(registro);
    }

    /**
     * Obtener historial de entrenamientos del usuario
     */
    @Transactional(readOnly = true)
    public List<RegistroEntrenamiento> obtenerHistorial(Long usuarioId) {
        return registroRepository.buscarPorUsuario(usuarioId);
    }

    /**
     * Obtener entrenamientos por rango de fechas
     */
    @Transactional(readOnly = true)
    public List<RegistroEntrenamiento> obtenerPorRango(Long usuarioId, LocalDate inicio, LocalDate fin) {
        return registroRepository.buscarPorUsuarioYRango(usuarioId, inicio, fin);
    }

    /**
     * Obtener estadísticas de entrenamiento del usuario
     */
    @Transactional(readOnly = true)
    public EstadisticasEntrenamiento obtenerEstadisticas(Long usuarioId) {
        List<RegistroEntrenamiento> registros = registroRepository.buscarPorUsuario(usuarioId);

        if (registros.isEmpty()) {
            return new EstadisticasEntrenamiento(0, 0, 0, 0.0, null, Map.of());
        }

        int totalEntrenamientos = registros.size();
        int entrenamientosCompletados = (int) registros.stream().filter(RegistroEntrenamiento::isCompletado).count();
        int totalMinutos = registros.stream()
                .mapToInt(r -> r.getDuracionMinutos() != null ? r.getDuracionMinutos() : 0)
                .sum();
        double promedioMinutos = registros.stream()
                .filter(r -> r.getDuracionMinutos() != null)
                .mapToInt(RegistroEntrenamiento::getDuracionMinutos)
                .average()
                .orElse(0.0);

        RegistroEntrenamiento ultimo = registros.get(0); // Ya está ordenado DESC

        // Entrenamientos por mes (últimos 6 meses)
        LocalDate hace6Meses = LocalDate.now().minusMonths(6);
        Map<String, Long> porMes = registros.stream()
                .filter(r -> r.getFechaEntrenamiento().toLocalDate().isAfter(hace6Meses))
                .collect(Collectors.groupingBy(
                        r -> r.getFechaEntrenamiento().getMonth().toString(),
                        Collectors.counting()));

        return new EstadisticasEntrenamiento(
                totalEntrenamientos,
                entrenamientosCompletados,
                totalMinutos,
                promedioMinutos,
                ultimo.getFechaEntrenamiento().toLocalDate(),
                porMes);
    }

    /**
     * Obtener el último entrenamiento del usuario
     */
    @Transactional(readOnly = true)
    public RegistroEntrenamiento obtenerUltimoEntrenamiento(Long usuarioId) {
        return registroRepository.buscarUltimoPorUsuario(usuarioId).orElse(null);
    }

    // ============ DTOs ============

    public record FinalizarEntrenamientoRequest(
            Integer duracionMinutos,
            String notas,
            Integer nivelEnergia, // 1-5
            Integer nivelSatisfaccion // 1-5
    ) {
    }

    public record EstadisticasEntrenamiento(
            int totalEntrenamientos,
            int entrenamientosCompletados,
            int totalMinutosEntrenados,
            double promedioMinutosPorSesion,
            LocalDate ultimoEntrenamiento,
            Map<String, Long> entrenamientosPorMes) {
    }
}
