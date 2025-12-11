package com.gym.backend.Shared.Domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface para operaciones de listado con paginación.
 * Aplicando Interface Segregation Principle (ISP).
 * 
 * @param <T> Tipo de entidad de dominio
 */
public interface BaseListPort<T> {

    /**
     * Lista todas las entidades
     */
    List<T> listar();

    /**
     * Lista entidades con paginación
     */
    Page<T> listarPaginated(Pageable pageable);
}
