package com.gym.backend.Fidelidad.Application;

import com.gym.backend.Fidelidad.Application.Dto.*;
import com.gym.backend.Fidelidad.Domain.*;
import com.gym.backend.Fidelidad.Domain.Enum.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Caso de uso principal para el sistema de puntos de fidelidad
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PuntosFidelidadUseCase {

    private final PuntosFidelidadRepositoryPort puntosRepository;
    private final TransaccionPuntosRepositoryPort transaccionRepository;
    private final RecompensaRepositoryPort recompensaRepository;
    private final CanjeRepositoryPort canjeRepository;

    // =========================================================
    // MÉTODOS PARA OBTENER INFORMACIÓN
    // =========================================================

    /**
     * Obtiene el balance de puntos de un usuario
     */
    public BalancePuntosDTO obtenerBalance(Long usuarioId) {
        PuntosFidelidad puntos = obtenerOCrearBalance(usuarioId);
        return mapToBalanceDTO(puntos);
    }

    /**
     * Obtiene el historial de transacciones de un usuario
     */
    public List<TransaccionPuntosDTO> obtenerHistorial(Long usuarioId, int pagina, int tamano) {
        return transaccionRepository.listarPorUsuarioPaginado(usuarioId, pagina, tamano)
                .stream()
                .map(this::mapToTransaccionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista todas las recompensas disponibles
     */
    public List<RecompensaDTO> listarRecompensasDisponibles() {
        return recompensaRepository.listarDisponibles()
                .stream()
                .map(this::mapToRecompensaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los canjes de un usuario
     */
    public List<CanjeDTO> obtenerCanjesUsuario(Long usuarioId) {
        return canjeRepository.listarPorUsuario(usuarioId)
                .stream()
                .map(this::mapToCanjeDTO)
                .collect(Collectors.toList());
    }

    // =========================================================
    // MÉTODOS PARA OTORGAR PUNTOS
    // =========================================================

    /**
     * Otorga puntos por una acción específica
     */
    @Transactional
    public BalancePuntosDTO otorgarPuntos(Long usuarioId, MotivoGanancia motivo,
            Long referenciaId, String tipoReferencia) {
        return otorgarPuntos(usuarioId, motivo, motivo.getPuntosBase(),
                motivo.getDescripcion(), referenciaId, tipoReferencia);
    }

    /**
     * Otorga una cantidad específica de puntos
     */
    @Transactional
    public BalancePuntosDTO otorgarPuntos(Long usuarioId, MotivoGanancia motivo, int puntos,
            String descripcion, Long referenciaId, String tipoReferencia) {
        if (puntos <= 0) {
            log.warn("Intento de otorgar {} puntos (debe ser positivo)", puntos);
            return obtenerBalance(usuarioId);
        }

        // Verificar que no sea duplicado (misma referencia y motivo)
        if (referenciaId != null && tipoReferencia != null) {
            if (transaccionRepository.existePorReferenciaYMotivo(referenciaId, tipoReferencia, motivo)) {
                log.warn("Puntos ya otorgados para referencia {}:{} con motivo {}",
                        tipoReferencia, referenciaId, motivo);
                return obtenerBalance(usuarioId);
            }
        }

        PuntosFidelidad balance = obtenerOCrearBalance(usuarioId);

        // Registrar transacción
        TransaccionPuntos transaccion = TransaccionPuntos.crearGanancia(
                usuarioId, motivo, puntos, descripcion, referenciaId, tipoReferencia);
        transaccionRepository.guardar(transaccion);

        // Actualizar balance (aplica multiplicador de nivel)
        balance.agregarPuntos(puntos);
        puntosRepository.guardar(balance);

        log.info("✅ Otorgados {} puntos a usuario {} por {}", puntos, usuarioId, motivo);

        return mapToBalanceDTO(balance);
    }

    /**
     * Otorga puntos por compra (10 pts por cada S/1)
     */
    @Transactional
    public BalancePuntosDTO otorgarPuntosPorCompra(Long usuarioId, Long pagoId, double monto) {
        int puntos = (int) (monto * 10); // 10 puntos por cada sol
        String descripcion = String.format("Compra por S/%.2f", monto);

        return otorgarPuntos(usuarioId, MotivoGanancia.COMPRA, puntos,
                descripcion, pagoId, "PAGO");
    }

    /**
     * Otorga puntos por asistencia diaria (máximo 1 vez por día)
     */
    @Transactional
    public BalancePuntosDTO otorgarPuntosPorAsistencia(Long usuarioId, Long asistenciaId) {
        // Verificar que no haya puntos por asistencia hoy
        java.time.LocalDateTime inicioHoy = java.time.LocalDate.now().atStartOfDay();
        long asistenciasHoy = transaccionRepository.contarPorUsuarioYMotivoDesde(
                usuarioId, MotivoGanancia.ASISTENCIA, inicioHoy);

        if (asistenciasHoy > 0) {
            log.debug("Usuario {} ya recibió puntos por asistencia hoy", usuarioId);
            return obtenerBalance(usuarioId);
        }

        return otorgarPuntos(usuarioId, MotivoGanancia.ASISTENCIA, asistenciaId, "ASISTENCIA");
    }

    // =========================================================
    // MÉTODOS PARA CANJEAR PUNTOS
    // =========================================================

    /**
     * Canjea puntos por una recompensa
     */
    @Transactional
    public CanjeDTO canjearPuntos(Long usuarioId, Long recompensaId) {
        // Obtener recompensa
        Recompensa recompensa = recompensaRepository.buscarPorId(recompensaId)
                .orElseThrow(() -> new IllegalArgumentException("Recompensa no encontrada"));

        // Validar disponibilidad
        if (!recompensa.estaDisponible()) {
            throw new IllegalStateException("La recompensa no está disponible");
        }

        // Obtener balance del usuario
        PuntosFidelidad balance = obtenerOCrearBalance(usuarioId);

        // Validar puntos suficientes
        if (!balance.tienePuntosSuficientes(recompensa.getCostoPuntos())) {
            throw new IllegalStateException(String.format(
                    "Puntos insuficientes. Tienes %d, necesitas %d",
                    balance.getPuntosDisponibles(), recompensa.getCostoPuntos()));
        }

        // Crear canje
        Canje canje = Canje.crear(usuarioId, recompensa);

        // Descontar puntos
        balance.canjearPuntos(recompensa.getCostoPuntos());
        puntosRepository.guardar(balance);

        // Reducir stock de recompensa
        recompensa.reducirStock();
        recompensaRepository.guardar(recompensa);

        // Registrar transacción
        TransaccionPuntos transaccion = TransaccionPuntos.crearCanje(
                usuarioId, recompensa.getCostoPuntos(),
                "Canje: " + recompensa.getNombre(), canje.getId());
        transaccionRepository.guardar(transaccion);

        // Guardar canje
        Canje canjeGuardado = canjeRepository.guardar(canje);

        log.info("🎁 Usuario {} canjeó {} puntos por '{}'",
                usuarioId, recompensa.getCostoPuntos(), recompensa.getNombre());

        return mapToCanjeDTO(canjeGuardado);
    }

    /**
     * Completa un canje pendiente (admin/recepcionista)
     */
    @Transactional
    public CanjeDTO completarCanje(Long canjeId) {
        Canje canje = canjeRepository.buscarPorId(canjeId)
                .orElseThrow(() -> new IllegalArgumentException("Canje no encontrado"));

        if (!canje.estaPendiente()) {
            throw new IllegalStateException("El canje no está pendiente");
        }

        canje.completar();
        Canje canjeActualizado = canjeRepository.guardar(canje);

        log.info("✅ Canje {} completado", canjeId);

        return mapToCanjeDTO(canjeActualizado);
    }

    /**
     * Cancela un canje pendiente (devuelve puntos)
     */
    @Transactional
    public CanjeDTO cancelarCanje(Long canjeId) {
        Canje canje = canjeRepository.buscarPorId(canjeId)
                .orElseThrow(() -> new IllegalArgumentException("Canje no encontrado"));

        if (!canje.estaPendiente()) {
            throw new IllegalStateException("Solo se pueden cancelar canjes pendientes");
        }

        // Devolver puntos al usuario
        PuntosFidelidad balance = obtenerOCrearBalance(canje.getUsuarioId());
        balance.devolverPuntos(canje.getPuntosUsados());
        puntosRepository.guardar(balance);

        // Restaurar stock de recompensa
        Recompensa recompensa = recompensaRepository.buscarPorId(canje.getRecompensaId()).orElse(null);
        if (recompensa != null) {
            recompensa.aumentarStock();
            recompensaRepository.guardar(recompensa);
        }

        // Cancelar canje
        canje.cancelar();
        Canje canjeActualizado = canjeRepository.guardar(canje);

        log.info("❌ Canje {} cancelado, {} puntos devueltos", canjeId, canje.getPuntosUsados());

        return mapToCanjeDTO(canjeActualizado);
    }

    // =========================================================
    // MÉTODOS ADMINISTRATIVOS
    // =========================================================

    /**
     * Ajusta puntos manualmente (admin)
     */
    @Transactional
    public BalancePuntosDTO ajustarPuntos(Long usuarioId, int puntos, String motivo) {
        PuntosFidelidad balance = obtenerOCrearBalance(usuarioId);

        // Registrar transacción
        TransaccionPuntos transaccion = TransaccionPuntos.crearAjuste(usuarioId, puntos, motivo);
        transaccionRepository.guardar(transaccion);

        // Aplicar ajuste
        if (puntos > 0) {
            balance.agregarPuntos(puntos);
        } else if (puntos < 0) {
            int puntosARestar = Math.min(Math.abs(puntos), balance.getPuntosDisponibles());
            balance.canjearPuntos(puntosARestar);
        }

        puntosRepository.guardar(balance);

        log.info("🔧 Ajuste de {} puntos para usuario {}: {}", puntos, usuarioId, motivo);

        return mapToBalanceDTO(balance);
    }

    /**
     * Lista canjes pendientes (para admin/recepcionista)
     */
    public List<CanjeDTO> listarCanjesPendientes() {
        return canjeRepository.listarPorEstado(EstadoCanje.PENDIENTE)
                .stream()
                .map(this::mapToCanjeDTO)
                .collect(Collectors.toList());
    }

    // =========================================================
    // MÉTODOS PRIVADOS
    // =========================================================

    /**
     * Obtiene el balance del usuario o lo crea si no existe
     */
    private PuntosFidelidad obtenerOCrearBalance(Long usuarioId) {
        return puntosRepository.buscarPorUsuarioId(usuarioId)
                .orElseGet(() -> {
                    PuntosFidelidad nuevo = PuntosFidelidad.crearNuevo(usuarioId);
                    return puntosRepository.guardar(nuevo);
                });
    }

    // =========================================================
    // MAPPERS
    // =========================================================

    private BalancePuntosDTO mapToBalanceDTO(PuntosFidelidad puntos) {
        NivelFidelidad siguienteNivel = null;
        if (puntos.getNivel() != NivelFidelidad.PLATINO) {
            siguienteNivel = NivelFidelidad.values()[puntos.getNivel().ordinal() + 1];
        }

        return BalancePuntosDTO.builder()
                .usuarioId(puntos.getUsuarioId())
                .puntosTotales(puntos.getPuntosTotales())
                .puntosDisponibles(puntos.getPuntosDisponibles())
                .puntosCanjeados(puntos.getPuntosCanjeados())
                .nivel(puntos.getNivel())
                .nombreNivel(puntos.getNivel().name())
                .multiplicadorNivel(puntos.getNivel().getMultiplicador())
                .progresoPorcentaje(puntos.progresoPorcentaje())
                .puntosParaSiguienteNivel(puntos.puntosParaSiguienteNivel())
                .siguienteNivel(siguienteNivel != null ? siguienteNivel.name() : null)
                .build();
    }

    private TransaccionPuntosDTO mapToTransaccionDTO(TransaccionPuntos t) {
        return TransaccionPuntosDTO.builder()
                .id(t.getId())
                .tipo(t.getTipo())
                .motivo(t.getMotivo())
                .motivoDescripcion(t.getMotivo() != null ? t.getMotivo().getDescripcion() : null)
                .puntos(t.getPuntos())
                .descripcion(t.getDescripcion())
                .fecha(t.getFecha())
                .esGanancia(t.esGanancia())
                .build();
    }

    private RecompensaDTO mapToRecompensaDTO(Recompensa r) {
        String tipoDesc = switch (r.getTipo()) {
            case DESCUENTO -> "Descuento";
            case EXTENSION -> "Días adicionales";
            case SERVICIO -> "Servicio";
            case PRODUCTO -> "Producto";
        };

        return RecompensaDTO.builder()
                .id(r.getId())
                .nombre(r.getNombre())
                .descripcion(r.getDescripcion())
                .costoPuntos(r.getCostoPuntos())
                .tipo(r.getTipo())
                .tipoDescripcion(tipoDesc)
                .valor(r.getValor())
                .stock(r.getStock())
                .disponible(r.estaDisponible())
                .imagenUrl(r.getImagenUrl())
                .build();
    }

    private CanjeDTO mapToCanjeDTO(Canje c) {
        return CanjeDTO.builder()
                .id(c.getId())
                .nombreRecompensa(c.getNombreRecompensa())
                .descripcionRecompensa(c.getDescripcionRecompensa())
                .puntosUsados(c.getPuntosUsados())
                .estado(c.getEstado())
                .codigoCanje(c.getCodigoCanje())
                .fechaCanje(c.getFechaCanje())
                .fechaUso(c.getFechaUso())
                .build();
    }
}
