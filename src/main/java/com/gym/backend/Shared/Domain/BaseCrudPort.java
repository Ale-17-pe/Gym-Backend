package com.gym.backend.Shared.Domain;

import java.util.Optional;

/**
 * Interface base para operaciones CRUD básicas.
 * Aplicando Interface Segregation Principle (ISP).
 * 
 * @param <T>  Tipo de entidad de dominio
 * @param <ID> Tipo del identificador
 */
public interface BaseCrudPort<T, ID> {

    /**
     * Guarda una nueva entidad
     */
    T guardar(T entity);

    /**
     * Actualiza una entidad existente
     */
    T actualizar(T entity);

    /**
     * Busca una entidad por su ID
     */
    Optional<T> buscarPorId(ID id);
}
