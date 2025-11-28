package com.gym.backend.Clases.Domain;

import com.gym.backend.Clases.Domain.Enum.EstadoSesion;
import com.gym.backend.Clases.Infrastructure.Entity.HorarioClaseEntity;
import com.gym.backend.Clases.Infrastructure.Entity.SesionClaseEntity;
import com.gym.backend.Clases.Infrastructure.Repository.HorarioClaseRepository;
import com.gym.backend.Clases.Infrastructure.Repository.SesionClaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SesionClaseUseCase {

    private final SesionClaseRepository sesionRepository;
    private final HorarioClaseRepository horarioRepository;

    /**
     * Obtener sesiones por rango de fechas
     */
    public List<SesionClaseEntity> obtenerPorRangoFechas(LocalDate inicio, LocalDate fin) {
        return sesionRepository.findByFechaBetween(inicio, fin);
    }

    /**
     * Obtener sesiones de un día específico
     */
    public List<SesionClaseEntity> obtenerPorFecha(LocalDate fecha) {
        return sesionRepository.findByFecha(fecha);
    }

    /**
     * Obtener sesiones de la semana actual
     */
    public List<SesionClaseEntity> obtenerSemanaActual() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);

        return obtenerPorRangoFechas(inicioSemana, finSemana);
    }

    /**
     * Generar sesiones para los próximos N días basándose en horarios recurrentes
     * Este método debería ejecutarse diariamente por un scheduler
     */
    public void generarSesionesFuturas(int diasAdelante) {
        LocalDate hoy = LocalDate.now();
        List<HorarioClaseEntity> horariosActivos = horarioRepository.findByActivoTrue();

        for (int i = 0; i < diasAdelante; i++) {
            LocalDate fecha = hoy.plusDays(i);
            int diaSemana = fecha.getDayOfWeek().getValue(); // 1=Lunes, 7=Domingo

            for (HorarioClaseEntity horario : horariosActivos) {
                if (horario.getDiaSemana().equals(diaSemana)) {
                    // Verificar si ya existe sesión para este horario y fecha
                    if (!sesionRepository.existsByHorarioClaseIdAndFecha(horario.getId(), fecha)) {
                        SesionClaseEntity sesion = SesionClaseEntity.builder()
                                .horarioClase(horario)
                                .fecha(fecha)
                                .estado(EstadoSesion.PROGRAMADA)
                                .asistentesActuales(0)
                                .build();

                        sesionRepository.save(sesion);
                    }
                }
            }
        }
    }

    /**
     * Cancelar una sesión
     */
    public void cancelarSesion(Long sesionId, String motivo) {
        SesionClaseEntity sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        sesion.setEstado(EstadoSesion.CANCELADA);
        sesion.setNotasInstructor(motivo);
        sesionRepository.save(sesion);

        // TODO: Notificar a todos los usuarios con reservas confirmadas
    }

    /**
     * Marcar sesión como completada
     */
    public void completarSesion(Long sesionId) {
        SesionClaseEntity sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        sesion.setEstado(EstadoSesion.COMPLETADA);
        sesionRepository.save(sesion);
    }
}
