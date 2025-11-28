package com.gym.backend.Clases.Infrastructure.Scheduler;

import com.gym.backend.Clases.Domain.SesionClaseUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SesionClaseScheduler {

    private final SesionClaseUseCase sesionClaseUseCase;

    /**
     * Ejecuta todos los días a las 00:00 para generar sesiones de los próximos 7
     * días
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void generarSesionesDiarias() {
        log.info("Iniciando generación automática de sesiones de clases");
        try {
            sesionClaseUseCase.generarSesionesFuturas(7);
            log.info("Sesiones generadas exitosamente");
        } catch (Exception e) {
            log.error("Error al generar sesiones automáticas", e);
        }
    }
}
