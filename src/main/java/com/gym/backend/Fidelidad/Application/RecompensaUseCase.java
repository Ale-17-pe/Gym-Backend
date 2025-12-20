package com.gym.backend.Fidelidad.Application;

import com.gym.backend.Fidelidad.Application.Dto.RecompensaDTO;
import com.gym.backend.Fidelidad.Domain.Recompensa;
import com.gym.backend.Fidelidad.Domain.RecompensaRepositoryPort;
import com.gym.backend.Fidelidad.Infrastructure.Controller.RecompensaRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Caso de uso para gestión de recompensas (Admin)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecompensaUseCase {

    private final RecompensaRepositoryPort recompensaRepository;

    public List<RecompensaDTO> listarTodas() {
        return recompensaRepository.listarTodas().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public RecompensaDTO obtener(Long id) {
        Recompensa recompensa = recompensaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Recompensa no encontrada"));
        return mapToDTO(recompensa);
    }

    @Transactional
    public RecompensaDTO crear(RecompensaRequest request) {
        Recompensa recompensa = Recompensa.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .costoPuntos(request.getCostoPuntos())
                .tipo(request.getTipo())
                .valor(request.getValor())
                .stock(request.getStock())
                .imagenUrl(request.getImagenUrl())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        recompensa = recompensaRepository.guardar(recompensa);
        log.info("Recompensa creada: {} (ID: {})", recompensa.getNombre(), recompensa.getId());
        return mapToDTO(recompensa);
    }

    @Transactional
    public RecompensaDTO actualizar(Long id, RecompensaRequest request) {
        Recompensa recompensa = recompensaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Recompensa no encontrada"));

        recompensa.setNombre(request.getNombre());
        recompensa.setDescripcion(request.getDescripcion());
        recompensa.setCostoPuntos(request.getCostoPuntos());
        recompensa.setTipo(request.getTipo());
        recompensa.setValor(request.getValor());
        recompensa.setStock(request.getStock());
        recompensa.setImagenUrl(request.getImagenUrl());
        recompensa.setFechaInicio(request.getFechaInicio());
        recompensa.setFechaFin(request.getFechaFin());

        recompensa = recompensaRepository.guardar(recompensa);
        log.info("Recompensa actualizada: {} (ID: {})", recompensa.getNombre(), recompensa.getId());
        return mapToDTO(recompensa);
    }

    @Transactional
    public void eliminar(Long id) {
        Recompensa recompensa = recompensaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Recompensa no encontrada"));
        recompensaRepository.eliminar(id);
        log.info("Recompensa eliminada: {} (ID: {})", recompensa.getNombre(), id);
    }

    @Transactional
    public RecompensaDTO activar(Long id) {
        Recompensa recompensa = recompensaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Recompensa no encontrada"));
        recompensa.setActivo(true);
        recompensa = recompensaRepository.guardar(recompensa);
        log.info("Recompensa activada: {} (ID: {})", recompensa.getNombre(), id);
        return mapToDTO(recompensa);
    }

    @Transactional
    public RecompensaDTO desactivar(Long id) {
        Recompensa recompensa = recompensaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Recompensa no encontrada"));
        recompensa.setActivo(false);
        recompensa = recompensaRepository.guardar(recompensa);
        log.info("Recompensa desactivada: {} (ID: {})", recompensa.getNombre(), id);
        return mapToDTO(recompensa);
    }

    private RecompensaDTO mapToDTO(Recompensa r) {
        String tipoDesc = switch (r.getTipo()) {
            case DESCUENTO -> "Descuento";
            case EXTENSION -> "Días extra";
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
}
