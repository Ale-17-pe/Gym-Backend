package com.gym.backend.Asistencias.Application;

import com.gym.backend.Asistencias.Domain.Asistencia;
import com.gym.backend.Asistencias.Domain.AsistenciaRepositoryPort;
import com.gym.backend.Fidelidad.Application.PuntosFidelidadUseCase;
import com.gym.backend.Fidelidad.Domain.Enum.MotivoGanancia;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestionar rachas de asistencia y puntos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RachaAsistenciaService {

    private final AsistenciaRepositoryPort asistenciaRepository;
    private final PuntosFidelidadUseCase puntosFidelidadUseCase;

    private static final int DIAS_PARA_RACHA = 7;
    private static final int PUNTOS_RACHA_7_DIAS = 100;

    /**
     * Procesa la asistencia y otorga puntos correspondientes
     */
    @Transactional
    public void procesarAsistencia(Long usuarioId, Long asistenciaId) {
        log.info("Procesando asistencia para usuario: {}", usuarioId);

        // 1. Otorgar puntos por asistencia diaria (máximo 1 vez por día)
        try {
            puntosFidelidadUseCase.otorgarPuntosPorAsistencia(usuarioId, asistenciaId);
        } catch (Exception e) {
            log.warn("No se pudieron otorgar puntos por asistencia: {}", e.getMessage());
        }

        // 2. Verificar si completó una racha de 7 días
        verificarRacha7Dias(usuarioId);
    }

    /**
     * Verifica si el usuario completó una racha de 7 días consecutivos
     */
    private void verificarRacha7Dias(Long usuarioId) {
        LocalDate hoy = LocalDate.now();
        int diasConsecutivos = 0;

        // Contar días consecutivos hacia atrás
        for (int i = 0; i < DIAS_PARA_RACHA; i++) {
            LocalDate fecha = hoy.minusDays(i);
            LocalDateTime inicioDelDia = fecha.atStartOfDay();
            LocalDateTime finDelDia = fecha.plusDays(1).atStartOfDay();

            List<Asistencia> asistenciasDelDia = asistenciaRepository.listarPorUsuarioYFecha(
                    usuarioId, inicioDelDia, finDelDia);

            if (asistenciasDelDia.isEmpty()) {
                break; // Se rompió la racha
            }
            diasConsecutivos++;
        }

        if (diasConsecutivos >= DIAS_PARA_RACHA) {
            // ¡Completó racha de 7 días!
            try {
                puntosFidelidadUseCase.otorgarPuntos(
                        usuarioId,
                        MotivoGanancia.RACHA_7_DIAS,
                        PUNTOS_RACHA_7_DIAS,
                        "¡Felicidades! Racha de 7 días consecutivos",
                        null,
                        "RACHA");
                log.info("🔥 Usuario {} completó racha de 7 días! +{} puntos", usuarioId, PUNTOS_RACHA_7_DIAS);
            } catch (Exception e) {
                log.warn("No se pudieron otorgar puntos por racha: {}", e.getMessage());
            }
        }
    }

    /**
     * Obtiene estadísticas de racha de un usuario
     */
    public RachaEstadisticas obtenerEstadisticas(Long usuarioId) {
        LocalDate hoy = LocalDate.now();
        int rachaActual = 0;
        int mejorRacha = 0;
        int rachaTemp = 0;

        // Calcular racha actual (días consecutivos hasta hoy)
        for (int i = 0; i < 365; i++) { // Máximo 1 año hacia atrás
            LocalDate fecha = hoy.minusDays(i);
            LocalDateTime inicioDelDia = fecha.atStartOfDay();
            LocalDateTime finDelDia = fecha.plusDays(1).atStartOfDay();

            List<Asistencia> asistenciasDelDia = asistenciaRepository.listarPorUsuarioYFecha(
                    usuarioId, inicioDelDia, finDelDia);

            if (!asistenciasDelDia.isEmpty()) {
                if (i == 0 || rachaTemp > 0) {
                    rachaTemp++;
                    if (i < 7)
                        rachaActual = rachaTemp; // Solo contar últimos 7 días para racha actual
                }
            } else {
                if (rachaTemp > mejorRacha) {
                    mejorRacha = rachaTemp;
                }
                rachaTemp = 0;
            }
        }

        if (rachaTemp > mejorRacha) {
            mejorRacha = rachaTemp;
        }

        return RachaEstadisticas.builder()
                .rachaActual(Math.min(rachaActual, rachaTemp))
                .mejorRacha(mejorRacha)
                .diasParaBonus(Math.max(0, DIAS_PARA_RACHA - rachaActual))
                .puntosProximoBonus(PUNTOS_RACHA_7_DIAS)
                .build();
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RachaEstadisticas {
        private int rachaActual;
        private int mejorRacha;
        private int diasParaBonus;
        private int puntosProximoBonus;
    }
}
