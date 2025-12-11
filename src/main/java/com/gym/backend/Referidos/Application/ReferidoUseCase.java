package com.gym.backend.Referidos.Application;

import com.gym.backend.Fidelidad.Application.PuntosFidelidadUseCase;
import com.gym.backend.Fidelidad.Domain.Enum.MotivoGanancia;
import com.gym.backend.Referidos.Domain.Referido;
import com.gym.backend.Referidos.Domain.Referido.EstadoReferido;
import com.gym.backend.Referidos.Domain.ReferidoRepositoryPort;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Domain.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Caso de uso para el sistema de referidos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReferidoUseCase {

    private final ReferidoRepositoryPort referidoRepository;
    private final UsuarioRepositoryPort usuarioRepository;
    private final PuntosFidelidadUseCase puntosFidelidadUseCase;

    private static final int PUNTOS_POR_REFERIDO = 200;

    /**
     * Genera un código de referido único para un usuario
     */
    public String generarCodigoReferido(Long usuarioId) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Generar código basado en iniciales + hash
        String iniciales = (usuario.getNombre().substring(0, 1) +
                usuario.getApellido().substring(0, 1)).toUpperCase();
        String hash = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        return "REF-" + iniciales + "-" + hash;
    }

    /**
     * Registra un nuevo referido cuando un usuario se registra con código
     */
    @Transactional
    public void registrarReferido(Long referidoId, String codigoReferido, Long referidorId) {
        // Verificar que el referidor existe
        if (!usuarioRepository.buscarPorId(referidorId).isPresent()) {
            log.warn("Referidor no encontrado: {}", referidorId);
            return;
        }

        // Verificar que no sea auto-referido
        if (referidorId.equals(referidoId)) {
            log.warn("Usuario {} intentó auto-referirse", referidoId);
            return;
        }

        // Verificar que el referido no tenga ya un referidor
        if (referidoRepository.buscarPorReferidoId(referidoId).isPresent()) {
            log.warn("Usuario {} ya tiene un referidor asignado", referidoId);
            return;
        }

        Referido referido = Referido.builder()
                .referidorId(referidorId)
                .referidoId(referidoId)
                .codigoReferido(codigoReferido)
                .estado(EstadoReferido.PENDIENTE)
                .fechaReferido(LocalDateTime.now())
                .build();

        referidoRepository.guardar(referido);
        log.info("👥 Referido registrado: Usuario {} referido por Usuario {} con código {}",
                referidoId, referidorId, codigoReferido);
    }

    /**
     * Completa un referido cuando el usuario hace su primer pago
     * Otorga puntos al referidor
     */
    @Transactional
    public void completarReferido(Long referidoId, Long pagoId) {
        Optional<Referido> optReferido = referidoRepository.buscarPorReferidoId(referidoId);

        if (optReferido.isEmpty()) {
            log.debug("Usuario {} no tiene referidor", referidoId);
            return;
        }

        Referido referido = optReferido.get();

        if (!referido.estaPendiente()) {
            log.debug("Referido ya fue completado o expirado");
            return;
        }

        // Completar el referido
        referido.completar(PUNTOS_POR_REFERIDO);
        referidoRepository.guardar(referido);

        // Otorgar puntos al referidor
        try {
            puntosFidelidadUseCase.otorgarPuntos(
                    referido.getReferidorId(),
                    MotivoGanancia.REFERIDO,
                    PUNTOS_POR_REFERIDO,
                    "Referido completó su primer pago",
                    referidoId,
                    "REFERIDO");

            log.info("🎉 Referido completado! Usuario {} ganó {} puntos por referir a Usuario {}",
                    referido.getReferidorId(), PUNTOS_POR_REFERIDO, referidoId);
        } catch (Exception e) {
            log.error("Error otorgando puntos por referido: {}", e.getMessage());
        }
    }

    /**
     * Obtiene los referidos de un usuario
     */
    public List<ReferidoDTO> obtenerMisReferidos(Long usuarioId) {
        return referidoRepository.listarPorReferidorId(usuarioId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Cuenta los referidos completados de un usuario
     */
    public long contarReferidosCompletados(Long usuarioId) {
        return referidoRepository.contarReferidosCompletados(usuarioId);
    }

    /**
     * Obtiene estadísticas de referidos de un usuario
     */
    public ReferidoEstadisticasDTO obtenerEstadisticas(Long usuarioId) {
        List<Referido> referidos = referidoRepository.listarPorReferidorId(usuarioId);

        long pendientes = referidos.stream().filter(r -> r.getEstado() == EstadoReferido.PENDIENTE).count();
        long completados = referidos.stream().filter(r -> r.getEstado() == EstadoReferido.COMPLETADO).count();
        int puntosGanados = referidos.stream()
                .filter(r -> r.getPuntosOtorgados() != null)
                .mapToInt(Referido::getPuntosOtorgados)
                .sum();

        return ReferidoEstadisticasDTO.builder()
                .totalReferidos(referidos.size())
                .referidosPendientes(pendientes)
                .referidosCompletados(completados)
                .puntosGanados(puntosGanados)
                .codigoReferido(generarCodigoReferido(usuarioId))
                .build();
    }

    private ReferidoDTO toDTO(Referido r) {
        // Obtener nombre del referido
        String nombreReferido = usuarioRepository.buscarPorId(r.getReferidoId())
                .map(u -> u.getNombre() + " " + u.getApellido().charAt(0) + ".")
                .orElse("Usuario");

        return ReferidoDTO.builder()
                .id(r.getId())
                .nombreReferido(nombreReferido)
                .estado(r.getEstado().name())
                .fechaReferido(r.getFechaReferido())
                .fechaCompletado(r.getFechaCompletado())
                .puntosOtorgados(r.getPuntosOtorgados())
                .build();
    }

    // DTOs internos
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ReferidoDTO {
        private Long id;
        private String nombreReferido;
        private String estado;
        private LocalDateTime fechaReferido;
        private LocalDateTime fechaCompletado;
        private Integer puntosOtorgados;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ReferidoEstadisticasDTO {
        private int totalReferidos;
        private long referidosPendientes;
        private long referidosCompletados;
        private int puntosGanados;
        private String codigoReferido;
    }
}
