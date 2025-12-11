package com.gym.backend.Shared.Infrastructure;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Clase para correcciones de base de datos en el arranque.
 * Solo ejecuta en PostgreSQL, no en H2 (tests).
 * 
 * NOTA: Considerar migrar a Flyway/Liquibase en producción.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseFixer {

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @PostConstruct
    public void fixMetodoPagoConstraint() {
        // Solo ejecutar en PostgreSQL, no en H2 (tests)
        if (datasourceUrl == null || !datasourceUrl.contains("postgresql")) {
            log.debug("Saltando DatabaseFixer - No es PostgreSQL (probablemente H2 para tests)");
            return;
        }

        try {
            log.info("Iniciando corrección de constraint pagos_metodo_pago_check...");

            // 1. Eliminar la constraint existente
            jdbcTemplate.execute("ALTER TABLE pagos DROP CONSTRAINT IF EXISTS pagos_metodo_pago_check");

            // 2. Crear la nueva constraint con todos los valores del Enum MetodoPago
            String sql = "ALTER TABLE pagos ADD CONSTRAINT pagos_metodo_pago_check CHECK (metodo_pago IN (" +
                    "'TARJETA_CREDITO', " +
                    "'TARJETA_DEBITO', " +
                    "'EFECTIVO', " +
                    "'TRANSFERENCIA_BANCARIA', " +
                    "'PSE', " +
                    "'PAYPAL', " +
                    "'OTRO', " +
                    "'BILLETERA_DIGITAL', " +
                    "'YAPE', " +
                    "'PLIN', " +
                    "'TRANSFERENCIA'" +
                    "))";

            jdbcTemplate.execute(sql);

            log.info("Constraint pagos_metodo_pago_check actualizada correctamente.");
        } catch (Exception e) {
            log.warn("No se pudo corregir constraint (puede ser normal en primera ejecución): {}", e.getMessage());
            // No lanzamos excepción para no detener el arranque
        }
    }
}
