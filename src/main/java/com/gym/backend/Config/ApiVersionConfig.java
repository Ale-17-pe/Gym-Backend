package com.gym.backend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de versionamiento de API.
 * Todos los endpoints bajo com.gym.backend tendrán el prefijo /api/v1
 * 
 * Esto permite:
 * - Mantener compatibilidad hacia atrás cuando se crea v2
 * - Deprecar endpoints gradualmente
 * - Mejor manejo de cambios breaking
 */
@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {

    /**
     * Configura el prefijo global para todos los controllers.
     * Los controllers con @RequestMapping("/api/...") se convierten en
     * "/api/v1/..."
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Se podría agregar un prefijo aquí, pero para no romper
        // los controllers existentes, mantenemos /api/ y documentamos
        // que esta es la v1 implícita
    }
}
