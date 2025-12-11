package com.gym.backend.Progreso.Application;

import com.gym.backend.Progreso.Domain.*;
import com.gym.backend.Progreso.Domain.Enum.TipoFoto;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Caso de uso para el seguimiento del progreso físico del cliente
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProgresoFisicoUseCase {

    private final MedidaCorporalRepositoryPort medidaRepository;
    private final FotoProgresoRepositoryPort fotoRepository;
    private final ObjetivoFisicoRepositoryPort objetivoRepository;

    // ============ MEDIDAS CORPORALES ============

    /**
     * Registrar nuevas medidas corporales
     */
    @Transactional
    public MedidaCorporal registrarMedidas(Long usuarioId, RegistrarMedidasRequest request) {
        log.info("Registrando medidas corporales para usuario: {}", usuarioId);

        // Verificar si ya existe registro para hoy
        LocalDate hoy = LocalDate.now();
        if (medidaRepository.buscarPorUsuarioYFecha(usuarioId, hoy).isPresent()) {
            log.info("Ya existe registro para hoy, actualizando...");
            return actualizarMedidasHoy(usuarioId, request);
        }

        MedidaCorporal medida = MedidaCorporal.builder()
                .usuarioId(usuarioId)
                .fecha(hoy)
                .pesoKg(request.pesoKg())
                .alturaM(request.alturaM())
                .pecho(request.pecho())
                .cintura(request.cintura())
                .cadera(request.cadera())
                .cuello(request.cuello())
                .brazoIzquierdo(request.brazoIzquierdo())
                .brazoDerecho(request.brazoDerecho())
                .antebrazoIzquierdo(request.antebrazoIzquierdo())
                .antebrazoDerecho(request.antebrazoDerecho())
                .musloIzquierdo(request.musloIzquierdo())
                .musloDerecho(request.musloDerecho())
                .pantorrillaIzquierda(request.pantorrillaIzquierda())
                .pantorrillaDerecha(request.pantorrillaDerecha())
                .hombros(request.hombros())
                .porcentajeGrasa(request.porcentajeGrasa())
                .porcentajeMusculo(request.porcentajeMusculo())
                .porcentajeAgua(request.porcentajeAgua())
                .masaOsea(request.masaOsea())
                .notas(request.notas())
                .fechaCreacion(LocalDateTime.now())
                .build();

        MedidaCorporal guardada = medidaRepository.guardar(medida);
        log.info("✅ Medidas registradas con ID: {}", guardada.getId());
        return guardada;
    }

    private MedidaCorporal actualizarMedidasHoy(Long usuarioId, RegistrarMedidasRequest request) {
        MedidaCorporal existente = medidaRepository.buscarPorUsuarioYFecha(usuarioId, LocalDate.now())
                .orElseThrow(() -> new IllegalStateException("No se encontró el registro"));

        if (request.pesoKg() != null)
            existente.setPesoKg(request.pesoKg());
        if (request.alturaM() != null)
            existente.setAlturaM(request.alturaM());
        if (request.pecho() != null)
            existente.setPecho(request.pecho());
        if (request.cintura() != null)
            existente.setCintura(request.cintura());
        if (request.cadera() != null)
            existente.setCadera(request.cadera());
        if (request.cuello() != null)
            existente.setCuello(request.cuello());
        if (request.brazoIzquierdo() != null)
            existente.setBrazoIzquierdo(request.brazoIzquierdo());
        if (request.brazoDerecho() != null)
            existente.setBrazoDerecho(request.brazoDerecho());
        if (request.musloIzquierdo() != null)
            existente.setMusloIzquierdo(request.musloIzquierdo());
        if (request.musloDerecho() != null)
            existente.setMusloDerecho(request.musloDerecho());
        if (request.hombros() != null)
            existente.setHombros(request.hombros());
        if (request.porcentajeGrasa() != null)
            existente.setPorcentajeGrasa(request.porcentajeGrasa());
        if (request.porcentajeMusculo() != null)
            existente.setPorcentajeMusculo(request.porcentajeMusculo());
        if (request.notas() != null)
            existente.setNotas(request.notas());

        return medidaRepository.guardar(existente);
    }

    /**
     * Obtener historial de medidas del usuario
     */
    @Transactional(readOnly = true)
    public List<MedidaCorporal> obtenerHistorialMedidas(Long usuarioId) {
        return medidaRepository.buscarPorUsuario(usuarioId);
    }

    /**
     * Obtener medidas por rango de fechas
     */
    @Transactional(readOnly = true)
    public List<MedidaCorporal> obtenerMedidasPorRango(Long usuarioId, LocalDate inicio, LocalDate fin) {
        return medidaRepository.buscarPorUsuarioYRango(usuarioId, inicio, fin);
    }

    /**
     * Obtener última medida registrada
     */
    @Transactional(readOnly = true)
    public MedidaCorporal obtenerUltimaMedida(Long usuarioId) {
        return medidaRepository.buscarUltimaPorUsuario(usuarioId).orElse(null);
    }

    /**
     * Calcular progreso entre dos fechas
     */
    @Transactional(readOnly = true)
    public ProgresoComparativo compararProgreso(Long usuarioId, LocalDate fechaInicio, LocalDate fechaFin) {
        MedidaCorporal medidaInicio = medidaRepository.buscarPorUsuarioYRango(usuarioId,
                fechaInicio.minusDays(7), fechaInicio.plusDays(7))
                .stream().findFirst().orElse(null);

        MedidaCorporal medidaFin = medidaRepository.buscarPorUsuarioYRango(usuarioId,
                fechaFin.minusDays(7), fechaFin.plusDays(7))
                .stream().reduce((a, b) -> b).orElse(null);

        if (medidaInicio == null || medidaFin == null) {
            return new ProgresoComparativo(null, null, new ArrayList<>());
        }

        List<CambioPorcentual> cambios = new ArrayList<>();

        if (medidaInicio.getPesoKg() != null && medidaFin.getPesoKg() != null) {
            double cambio = medidaFin.getPesoKg() - medidaInicio.getPesoKg();
            double porcentaje = (cambio / medidaInicio.getPesoKg()) * 100;
            cambios.add(
                    new CambioPorcentual("Peso", medidaInicio.getPesoKg(), medidaFin.getPesoKg(), cambio, porcentaje));
        }

        if (medidaInicio.getCintura() != null && medidaFin.getCintura() != null) {
            double cambio = medidaFin.getCintura() - medidaInicio.getCintura();
            double porcentaje = (cambio / medidaInicio.getCintura()) * 100;
            cambios.add(new CambioPorcentual("Cintura", medidaInicio.getCintura(), medidaFin.getCintura(), cambio,
                    porcentaje));
        }

        if (medidaInicio.getBrazoIzquierdo() != null && medidaFin.getBrazoIzquierdo() != null) {
            double cambio = medidaFin.getBrazoIzquierdo() - medidaInicio.getBrazoIzquierdo();
            double porcentaje = (cambio / medidaInicio.getBrazoIzquierdo()) * 100;
            cambios.add(new CambioPorcentual("Brazo", medidaInicio.getBrazoIzquierdo(), medidaFin.getBrazoIzquierdo(),
                    cambio, porcentaje));
        }

        if (medidaInicio.getPecho() != null && medidaFin.getPecho() != null) {
            double cambio = medidaFin.getPecho() - medidaInicio.getPecho();
            double porcentaje = (cambio / medidaInicio.getPecho()) * 100;
            cambios.add(
                    new CambioPorcentual("Pecho", medidaInicio.getPecho(), medidaFin.getPecho(), cambio, porcentaje));
        }

        return new ProgresoComparativo(medidaInicio, medidaFin, cambios);
    }

    // ============ FOTOS DE PROGRESO ============

    /**
     * Registrar una foto de progreso
     */
    @Transactional
    public FotoProgreso registrarFoto(Long usuarioId, RegistrarFotoRequest request) {
        log.info("Registrando foto de progreso para usuario: {}", usuarioId);

        // Obtener peso actual si existe
        Double pesoActual = medidaRepository.buscarUltimaPorUsuario(usuarioId)
                .map(MedidaCorporal::getPesoKg)
                .orElse(null);

        FotoProgreso foto = FotoProgreso.builder()
                .usuarioId(usuarioId)
                .fecha(LocalDate.now())
                .tipoFoto(request.tipoFoto())
                .urlImagen(request.urlImagen())
                .nombreArchivo(request.nombreArchivo())
                .tamanoBytes(request.tamanoBytes())
                .notas(request.notas())
                .pesoEnFoto(pesoActual)
                .fechaCreacion(LocalDateTime.now())
                .build();

        FotoProgreso guardada = fotoRepository.guardar(foto);
        log.info("✅ Foto registrada con ID: {}", guardada.getId());
        return guardada;
    }

    /**
     * Obtener todas las fotos del usuario
     */
    @Transactional(readOnly = true)
    public List<FotoProgreso> obtenerFotos(Long usuarioId) {
        return fotoRepository.buscarPorUsuario(usuarioId);
    }

    /**
     * Obtener fotos por tipo
     */
    @Transactional(readOnly = true)
    public List<FotoProgreso> obtenerFotosPorTipo(Long usuarioId, TipoFoto tipoFoto) {
        return fotoRepository.buscarPorUsuarioYTipo(usuarioId, tipoFoto);
    }

    /**
     * Obtener fotos agrupadas por fecha para comparación
     */
    @Transactional(readOnly = true)
    public Map<LocalDate, List<FotoProgreso>> obtenerFotosAgrupadas(Long usuarioId) {
        return fotoRepository.buscarPorUsuario(usuarioId).stream()
                .collect(Collectors.groupingBy(FotoProgreso::getFecha));
    }

    /**
     * Eliminar una foto
     */
    @Transactional
    public void eliminarFoto(Long fotoId, Long usuarioId) {
        FotoProgreso foto = fotoRepository.buscarPorId(fotoId)
                .orElseThrow(() -> new IllegalArgumentException("Foto no encontrada"));

        if (!foto.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para eliminar esta foto");
        }

        fotoRepository.eliminar(fotoId);
        log.info("Foto eliminada: {}", fotoId);
    }

    // ============ OBJETIVOS ============

    /**
     * Crear un objetivo físico
     */
    @Transactional
    public ObjetivoFisico crearObjetivo(Long usuarioId, CrearObjetivoRequest request) {
        log.info("Creando objetivo para usuario: {}", usuarioId);

        // Desactivar objetivo anterior si existe
        objetivoRepository.buscarActivoPorUsuario(usuarioId).ifPresent(obj -> {
            obj.setActivo(false);
            objetivoRepository.guardar(obj);
        });

        // Obtener peso actual
        Double pesoActual = medidaRepository.buscarUltimaPorUsuario(usuarioId)
                .map(MedidaCorporal::getPesoKg)
                .orElse(request.pesoActual());

        ObjetivoFisico objetivo = ObjetivoFisico.builder()
                .usuarioId(usuarioId)
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .pesoActual(pesoActual)
                .pesoObjetivo(request.pesoObjetivo())
                .cinturaObjetivo(request.cinturaObjetivo())
                .porcentajeGrasaObjetivo(request.porcentajeGrasaObjetivo())
                .fechaInicio(LocalDate.now())
                .fechaObjetivo(request.fechaObjetivo())
                .activo(true)
                .completado(false)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        ObjetivoFisico guardado = objetivoRepository.guardar(objetivo);
        log.info("✅ Objetivo creado: {}", guardado.getNombre());
        return guardado;
    }

    /**
     * Obtener objetivo activo
     */
    @Transactional(readOnly = true)
    public ObjetivoFisico obtenerObjetivoActivo(Long usuarioId) {
        return objetivoRepository.buscarActivoPorUsuario(usuarioId).orElse(null);
    }

    /**
     * Obtener todos los objetivos del usuario
     */
    @Transactional(readOnly = true)
    public List<ObjetivoFisico> obtenerObjetivos(Long usuarioId) {
        return objetivoRepository.buscarPorUsuario(usuarioId);
    }

    /**
     * Marcar objetivo como completado
     */
    @Transactional
    public ObjetivoFisico completarObjetivo(Long objetivoId, Long usuarioId) {
        ObjetivoFisico objetivo = objetivoRepository.buscarPorId(objetivoId)
                .orElseThrow(() -> new IllegalArgumentException("Objetivo no encontrado"));

        if (!objetivo.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para este objetivo");
        }

        objetivo.setCompletado(true);
        objetivo.setActivo(false);
        objetivo.setFechaCompletado(LocalDate.now());
        objetivo.setFechaActualizacion(LocalDateTime.now());

        log.info("🎉 Objetivo completado: {}", objetivo.getNombre());
        return objetivoRepository.guardar(objetivo);
    }

    /**
     * Obtener resumen completo del progreso
     */
    @Transactional(readOnly = true)
    public ResumenProgreso obtenerResumen(Long usuarioId) {
        MedidaCorporal ultimaMedida = medidaRepository.buscarUltimaPorUsuario(usuarioId).orElse(null);
        ObjetivoFisico objetivoActivo = objetivoRepository.buscarActivoPorUsuario(usuarioId).orElse(null);
        int totalFotos = fotoRepository.contarPorUsuario(usuarioId);
        int totalMedidas = medidaRepository.buscarPorUsuario(usuarioId).size();
        int objetivosCompletados = objetivoRepository.buscarCompletadosPorUsuario(usuarioId).size();

        // Calcular progreso hacia objetivo
        Double progresoObjetivo = null;
        if (objetivoActivo != null && ultimaMedida != null) {
            progresoObjetivo = objetivoActivo.calcularProgresoPeso(ultimaMedida.getPesoKg());
        }

        return new ResumenProgreso(
                ultimaMedida,
                objetivoActivo,
                progresoObjetivo,
                totalFotos,
                totalMedidas,
                objetivosCompletados);
    }

    // ============ DTOs ============

    public record RegistrarMedidasRequest(
            Double pesoKg,
            Double alturaM,
            Double pecho,
            Double cintura,
            Double cadera,
            Double cuello,
            Double brazoIzquierdo,
            Double brazoDerecho,
            Double antebrazoIzquierdo,
            Double antebrazoDerecho,
            Double musloIzquierdo,
            Double musloDerecho,
            Double pantorrillaIzquierda,
            Double pantorrillaDerecha,
            Double hombros,
            Double porcentajeGrasa,
            Double porcentajeMusculo,
            Double porcentajeAgua,
            Double masaOsea,
            String notas) {
    }

    public record RegistrarFotoRequest(
            TipoFoto tipoFoto,
            String urlImagen,
            String nombreArchivo,
            Long tamanoBytes,
            String notas) {
    }

    public record CrearObjetivoRequest(
            String nombre,
            String descripcion,
            Double pesoActual,
            Double pesoObjetivo,
            Double cinturaObjetivo,
            Double porcentajeGrasaObjetivo,
            LocalDate fechaObjetivo) {
    }

    public record ProgresoComparativo(
            MedidaCorporal medidaInicio,
            MedidaCorporal medidaFin,
            List<CambioPorcentual> cambios) {
    }

    public record CambioPorcentual(
            String campo,
            Double valorInicial,
            Double valorFinal,
            Double cambioAbsoluto,
            Double cambioPorcentual) {
    }

    public record ResumenProgreso(
            MedidaCorporal ultimaMedida,
            ObjetivoFisico objetivoActivo,
            Double progresoObjetivoActual,
            int totalFotos,
            int totalMedidas,
            int objetivosCompletados) {
    }
}
