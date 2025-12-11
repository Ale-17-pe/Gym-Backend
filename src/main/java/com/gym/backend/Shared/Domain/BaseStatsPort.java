package com.gym.backend.Shared.Domain;

/**
 * Interface para operaciones de estadísticas.
 * Aplicando Interface Segregation Principle (ISP).
 */
public interface BaseStatsPort {

    /**
     * Cuenta el total de entidades
     */
    Long contarTotal();
}
