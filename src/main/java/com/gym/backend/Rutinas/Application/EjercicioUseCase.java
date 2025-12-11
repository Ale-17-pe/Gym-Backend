package com.gym.backend.Rutinas.Application;

import com.gym.backend.Rutinas.Domain.*;
import com.gym.backend.Rutinas.Domain.Enum.GrupoMuscular;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de uso para gestionar el catálogo de ejercicios
 * Solo administradores pueden crear/editar ejercicios
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EjercicioUseCase {

    private final EjercicioRepositoryPort ejercicioRepository;

    /**
     * Crear un nuevo ejercicio en el catálogo
     */
    @Transactional
    public Ejercicio crear(Ejercicio ejercicio) {
        log.info("Creando ejercicio: {}", ejercicio.getNombre());
        ejercicio.setActivo(true);
        ejercicio.setFechaCreacion(LocalDateTime.now());
        ejercicio.setFechaActualizacion(LocalDateTime.now());
        return ejercicioRepository.guardar(ejercicio);
    }

    /**
     * Actualizar un ejercicio existente
     */
    @Transactional
    public Ejercicio actualizar(Long id, Ejercicio datos) {
        log.info("Actualizando ejercicio ID: {}", id);
        Ejercicio existente = ejercicioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado"));

        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setGrupoMuscular(datos.getGrupoMuscular());
        existente.setGrupoMuscularSecundario(datos.getGrupoMuscularSecundario());
        existente.setDificultad(datos.getDificultad());
        existente.setEquipamiento(datos.getEquipamiento());
        existente.setInstrucciones(datos.getInstrucciones());
        existente.setImagenUrl(datos.getImagenUrl());
        existente.setVideoUrl(datos.getVideoUrl());
        existente.setFechaActualizacion(LocalDateTime.now());

        return ejercicioRepository.guardar(existente);
    }

    /**
     * Obtener un ejercicio por ID
     */
    @Transactional(readOnly = true)
    public Ejercicio obtener(Long id) {
        return ejercicioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado"));
    }

    /**
     * Listar todos los ejercicios activos
     */
    @Transactional(readOnly = true)
    public List<Ejercicio> listarActivos() {
        return ejercicioRepository.listarActivos();
    }

    /**
     * Buscar ejercicios por grupo muscular
     */
    @Transactional(readOnly = true)
    public List<Ejercicio> buscarPorGrupoMuscular(GrupoMuscular grupoMuscular) {
        return ejercicioRepository.buscarPorGrupoMuscular(grupoMuscular);
    }

    /**
     * Buscar ejercicios por nombre
     */
    @Transactional(readOnly = true)
    public List<Ejercicio> buscarPorNombre(String nombre) {
        return ejercicioRepository.buscarPorNombre(nombre);
    }

    /**
     * Desactivar un ejercicio (no eliminar físicamente)
     */
    @Transactional
    public void desactivar(Long id) {
        log.info("Desactivando ejercicio ID: {}", id);
        Ejercicio ejercicio = obtener(id);
        ejercicio.setActivo(false);
        ejercicio.setFechaActualizacion(LocalDateTime.now());
        ejercicioRepository.guardar(ejercicio);
    }

    /**
     * Reactivar un ejercicio
     */
    @Transactional
    public void activar(Long id) {
        log.info("Activando ejercicio ID: {}", id);
        Ejercicio ejercicio = obtener(id);
        ejercicio.setActivo(true);
        ejercicio.setFechaActualizacion(LocalDateTime.now());
        ejercicioRepository.guardar(ejercicio);
    }
}
