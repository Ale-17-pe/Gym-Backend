package com.gym.backend.Shared.Cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuración de caché en memoria para almacenamiento temporal
 * - Códigos 2FA (expiran en 5 minutos)
 * - Códigos de recuperación de contraseña (expiran en 15 minutos)
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("two-factor-codes"),
                new ConcurrentMapCache("password-reset-codes"),
                new ConcurrentMapCache("email-verification-codes")));
        return cacheManager;
    }
}
