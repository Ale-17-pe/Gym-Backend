package com.gym.backend.Fidelidad.Infrastructure;

import com.gym.backend.Fidelidad.Domain.Enum.TipoRecompensa;
import com.gym.backend.Fidelidad.Domain.Recompensa;
import com.gym.backend.Fidelidad.Domain.RecompensaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Inicializa el catálogo de recompensas si está vacío
 */
@Slf4j
@Component
@Order(10) // Ejecutar después de otros inicializadores
@RequiredArgsConstructor
public class RecompensasDataLoader implements CommandLineRunner {

    private final RecompensaRepositoryPort recompensaRepository;

    @Override
    public void run(String... args) {
        if (!recompensaRepository.listarTodas().isEmpty()) {
            log.info("🎁 Catálogo de recompensas ya inicializado");
            return;
        }

        log.info("🎁 Inicializando catálogo de recompensas...");

        List<Recompensa> recompensas = List.of(
                // Descuentos
                Recompensa.builder()
                        .nombre("Descuento 10%")
                        .descripcion("10% de descuento en tu próxima membresía")
                        .costoPuntos(500)
                        .tipo(TipoRecompensa.DESCUENTO)
                        .valor(BigDecimal.valueOf(10))
                        .activo(true)
                        .build(),

                Recompensa.builder()
                        .nombre("Descuento 20%")
                        .descripcion("20% de descuento en tu próxima membresía")
                        .costoPuntos(900)
                        .tipo(TipoRecompensa.DESCUENTO)
                        .valor(BigDecimal.valueOf(20))
                        .activo(true)
                        .build(),

                Recompensa.builder()
                        .nombre("Descuento 30%")
                        .descripcion("30% de descuento en tu próxima membresía")
                        .costoPuntos(1300)
                        .tipo(TipoRecompensa.DESCUENTO)
                        .valor(BigDecimal.valueOf(30))
                        .activo(true)
                        .build(),

                // Extensiones de membresía
                Recompensa.builder()
                        .nombre("1 Día Gratis")
                        .descripcion("Extiende tu membresía por 1 día adicional")
                        .costoPuntos(300)
                        .tipo(TipoRecompensa.EXTENSION)
                        .valor(BigDecimal.valueOf(1))
                        .activo(true)
                        .build(),

                Recompensa.builder()
                        .nombre("1 Semana Gratis")
                        .descripcion("Extiende tu membresía por 7 días adicionales")
                        .costoPuntos(800)
                        .tipo(TipoRecompensa.EXTENSION)
                        .valor(BigDecimal.valueOf(7))
                        .activo(true)
                        .build(),

                // Servicios
                Recompensa.builder()
                        .nombre("Clase Personal Gratis")
                        .descripcion("Una sesión de entrenamiento personal con instructor")
                        .costoPuntos(1000)
                        .tipo(TipoRecompensa.SERVICIO)
                        .valor(BigDecimal.valueOf(1))
                        .activo(true)
                        .build(),

                Recompensa.builder()
                        .nombre("Evaluación Física")
                        .descripcion("Evaluación física completa con nutricionista")
                        .costoPuntos(700)
                        .tipo(TipoRecompensa.SERVICIO)
                        .valor(BigDecimal.valueOf(1))
                        .activo(true)
                        .build(),

                // Productos (merchandising)
                Recompensa.builder()
                        .nombre("Toalla AresFitness")
                        .descripcion("Toalla deportiva con logo del gimnasio")
                        .costoPuntos(600)
                        .tipo(TipoRecompensa.PRODUCTO)
                        .valor(BigDecimal.valueOf(1))
                        .stock(50) // Stock limitado
                        .activo(true)
                        .build(),

                Recompensa.builder()
                        .nombre("Botella Deportiva")
                        .descripcion("Botella de agua reutilizable 750ml")
                        .costoPuntos(400)
                        .tipo(TipoRecompensa.PRODUCTO)
                        .valor(BigDecimal.valueOf(1))
                        .stock(100)
                        .activo(true)
                        .build(),

                Recompensa.builder()
                        .nombre("Camiseta AresFitness")
                        .descripcion("Camiseta deportiva oficial del gimnasio")
                        .costoPuntos(1200)
                        .tipo(TipoRecompensa.PRODUCTO)
                        .valor(BigDecimal.valueOf(1))
                        .stock(30)
                        .activo(true)
                        .build());

        for (Recompensa r : recompensas) {
            recompensaRepository.guardar(r);
        }

        log.info("✅ {} recompensas inicializadas correctamente", recompensas.size());
    }
}
