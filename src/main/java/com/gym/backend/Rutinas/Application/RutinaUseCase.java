package com.gym.backend.Rutinas.Application;

import com.gym.backend.Rutinas.Domain.*;
import com.gym.backend.Rutinas.Domain.Enum.DiaSemana;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso para gestionar las rutinas del cliente
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RutinaUseCase {

    private final RutinaRepositoryPort rutinaRepository;
    private final DiaRutinaRepositoryPort diaRutinaRepository;
    private final EjercicioRutinaRepositoryPort ejercicioRutinaRepository;
    private final EjercicioRepositoryPort ejercicioRepository;

    /**
     * Crear una nueva rutina para el usuario
     */
    @Transactional
    public Rutina crearRutina(Long usuarioId, CrearRutinaRequest request) {
        log.info("Creando rutina '{}' para usuario: {}", request.nombre(), usuarioId);

        Rutina rutina = Rutina.builder()
                .usuarioId(usuarioId)
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .objetivo(request.objetivo())
                .duracionSemanas(request.duracionSemanas())
                .activa(false)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        Rutina guardada = rutinaRepository.guardar(rutina);

        // Crear los 7 días de la semana
        for (DiaSemana dia : DiaSemana.values()) {
            DiaRutina diaRutina = DiaRutina.builder()
                    .rutinaId(guardada.getId())
                    .diaSemana(dia)
                    .nombre(dia.getNombre())
                    .esDescanso(dia == DiaSemana.DOMINGO) // Por defecto domingo es descanso
                    .orden(dia.getNumero())
                    .build();
            diaRutinaRepository.guardar(diaRutina);
        }

        log.info("✅ Rutina creada con ID: {}", guardada.getId());
        return guardada;
    }

    /**
     * Obtener rutina por ID con todos sus días y ejercicios
     */
    @Transactional(readOnly = true)
    public RutinaCompleta obtenerRutinaCompleta(Long rutinaId) {
        Rutina rutina = rutinaRepository.buscarPorId(rutinaId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));

        List<DiaRutina> dias = diaRutinaRepository.buscarPorRutina(rutinaId);
        List<DiaRutinaCompleto> diasCompletos = new ArrayList<>();

        for (DiaRutina dia : dias) {
            List<EjercicioRutina> ejercicios = ejercicioRutinaRepository.buscarPorDiaRutina(dia.getId());

            // Enriquecer con datos del ejercicio
            for (EjercicioRutina er : ejercicios) {
                ejercicioRepository.buscarPorId(er.getEjercicioId())
                        .ifPresent(er::setEjercicio);
            }

            diasCompletos.add(new DiaRutinaCompleto(dia, ejercicios));
        }

        return new RutinaCompleta(rutina, diasCompletos);
    }

    /**
     * Listar todas las rutinas del usuario
     */
    @Transactional(readOnly = true)
    public List<Rutina> listarRutinasUsuario(Long usuarioId) {
        return rutinaRepository.buscarPorUsuario(usuarioId);
    }

    /**
     * Obtener la rutina activa del usuario
     */
    @Transactional(readOnly = true)
    public Rutina obtenerRutinaActiva(Long usuarioId) {
        return rutinaRepository.buscarActivaPorUsuario(usuarioId).orElse(null);
    }

    /**
     * Activar una rutina (desactiva las demás)
     */
    @Transactional
    public Rutina activarRutina(Long rutinaId, Long usuarioId) {
        log.info("Activando rutina {} para usuario {}", rutinaId, usuarioId);

        Rutina rutina = rutinaRepository.buscarPorId(rutinaId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));

        if (!rutina.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para esta rutina");
        }

        // Desactivar todas las rutinas del usuario
        rutinaRepository.desactivarTodasPorUsuario(usuarioId);

        // Activar la seleccionada
        rutina.activar();
        return rutinaRepository.guardar(rutina);
    }

    /**
     * Actualizar información de una rutina
     */
    @Transactional
    public Rutina actualizarRutina(Long rutinaId, Long usuarioId, ActualizarRutinaRequest request) {
        Rutina rutina = rutinaRepository.buscarPorId(rutinaId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));

        if (!rutina.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para esta rutina");
        }

        rutina.setNombre(request.nombre());
        rutina.setDescripcion(request.descripcion());
        rutina.setObjetivo(request.objetivo());
        rutina.setDuracionSemanas(request.duracionSemanas());
        rutina.setFechaActualizacion(LocalDateTime.now());

        return rutinaRepository.guardar(rutina);
    }

    /**
     * Actualizar un día de la rutina
     */
    @Transactional
    public DiaRutina actualizarDiaRutina(Long diaRutinaId, Long usuarioId, ActualizarDiaRequest request) {
        DiaRutina dia = diaRutinaRepository.buscarPorId(diaRutinaId)
                .orElseThrow(() -> new IllegalArgumentException("Día de rutina no encontrado"));

        // Verificar pertenencia
        Rutina rutina = rutinaRepository.buscarPorId(dia.getRutinaId())
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));
        if (!rutina.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para esta rutina");
        }

        dia.setNombre(request.nombre());
        dia.setNotas(request.notas());
        dia.setEsDescanso(request.esDescanso());

        return diaRutinaRepository.guardar(dia);
    }

    /**
     * Agregar un ejercicio a un día de la rutina
     */
    @Transactional
    public EjercicioRutina agregarEjercicio(Long diaRutinaId, Long usuarioId, AgregarEjercicioRequest request) {
        DiaRutina dia = diaRutinaRepository.buscarPorId(diaRutinaId)
                .orElseThrow(() -> new IllegalArgumentException("Día de rutina no encontrado"));

        // Verificar pertenencia
        Rutina rutina = rutinaRepository.buscarPorId(dia.getRutinaId())
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));
        if (!rutina.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para esta rutina");
        }

        // Verificar que el ejercicio existe
        Ejercicio ejercicio = ejercicioRepository.buscarPorId(request.ejercicioId())
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado"));

        // Obtener el orden máximo actual
        List<EjercicioRutina> existentes = ejercicioRutinaRepository.buscarPorDiaRutina(diaRutinaId);
        int nuevoOrden = existentes.size() + 1;

        EjercicioRutina nuevo = EjercicioRutina.builder()
                .diaRutinaId(diaRutinaId)
                .ejercicioId(request.ejercicioId())
                .nombreEjercicio(ejercicio.getNombre())
                .series(request.series())
                .repeticiones(request.repeticiones())
                .rangoRepeticiones(request.rangoRepeticiones())
                .descansoSegundos(request.descansoSegundos())
                .pesoSugerido(request.pesoSugerido())
                .orden(nuevoOrden)
                .notas(request.notas())
                .tempo(request.tempo())
                .build();

        log.info("Agregando ejercicio '{}' al día {}", ejercicio.getNombre(), dia.getNombre());
        return ejercicioRutinaRepository.guardar(nuevo);
    }

    /**
     * Actualizar un ejercicio de la rutina
     */
    @Transactional
    public EjercicioRutina actualizarEjercicioRutina(Long ejercicioRutinaId, Long usuarioId,
            ActualizarEjercicioRutinaRequest request) {
        EjercicioRutina er = ejercicioRutinaRepository.buscarPorId(ejercicioRutinaId)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado en la rutina"));

        // Verificar pertenencia
        DiaRutina dia = diaRutinaRepository.buscarPorId(er.getDiaRutinaId())
                .orElseThrow(() -> new IllegalArgumentException("Día de rutina no encontrado"));
        Rutina rutina = rutinaRepository.buscarPorId(dia.getRutinaId())
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));
        if (!rutina.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para esta rutina");
        }

        er.setSeries(request.series());
        er.setRepeticiones(request.repeticiones());
        er.setRangoRepeticiones(request.rangoRepeticiones());
        er.setDescansoSegundos(request.descansoSegundos());
        er.setPesoSugerido(request.pesoSugerido());
        er.setNotas(request.notas());
        er.setTempo(request.tempo());

        return ejercicioRutinaRepository.guardar(er);
    }

    /**
     * Eliminar un ejercicio de la rutina
     */
    @Transactional
    public void eliminarEjercicioRutina(Long ejercicioRutinaId, Long usuarioId) {
        EjercicioRutina er = ejercicioRutinaRepository.buscarPorId(ejercicioRutinaId)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado en la rutina"));

        // Verificar pertenencia
        DiaRutina dia = diaRutinaRepository.buscarPorId(er.getDiaRutinaId())
                .orElseThrow(() -> new IllegalArgumentException("Día de rutina no encontrado"));
        Rutina rutina = rutinaRepository.buscarPorId(dia.getRutinaId())
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));
        if (!rutina.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para esta rutina");
        }

        ejercicioRutinaRepository.eliminar(ejercicioRutinaId);
        log.info("Ejercicio eliminado de la rutina");
    }

    /**
     * Eliminar una rutina completa
     */
    @Transactional
    public void eliminarRutina(Long rutinaId, Long usuarioId) {
        Rutina rutina = rutinaRepository.buscarPorId(rutinaId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));

        if (!rutina.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para esta rutina");
        }

        log.info("Eliminando rutina: {}", rutina.getNombre());
        rutinaRepository.eliminar(rutinaId);
    }

    // ============ DTOs ============

    public record CrearRutinaRequest(
            String nombre,
            String descripcion,
            String objetivo,
            Integer duracionSemanas) {
    }

    public record ActualizarRutinaRequest(
            String nombre,
            String descripcion,
            String objetivo,
            Integer duracionSemanas) {
    }

    public record ActualizarDiaRequest(
            String nombre,
            String notas,
            boolean esDescanso) {
    }

    public record AgregarEjercicioRequest(
            Long ejercicioId,
            Integer series,
            Integer repeticiones,
            String rangoRepeticiones,
            Integer descansoSegundos,
            Double pesoSugerido,
            String notas,
            String tempo) {
    }

    public record ActualizarEjercicioRutinaRequest(
            Integer series,
            Integer repeticiones,
            String rangoRepeticiones,
            Integer descansoSegundos,
            Double pesoSugerido,
            String notas,
            String tempo) {
    }

    public record RutinaCompleta(
            Rutina rutina,
            List<DiaRutinaCompleto> dias) {
    }

    public record DiaRutinaCompleto(
            DiaRutina dia,
            List<EjercicioRutina> ejercicios) {
    }
}
