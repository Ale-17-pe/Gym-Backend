package com.gym.backend.Notificacion.Application;

import com.gym.backend.Notificacion.Domain.Notificacion;
import com.gym.backend.Notificacion.Domain.TipoNotificacion;
import com.gym.backend.Notificacion.DTO.NotificacionDTO;
import com.gym.backend.Notificacion.Infrastructure.NotificacionRepository;
import com.gym.backend.Shared.Email.EmailService;
import com.gym.backend.Usuarios.Application.Dto.UsuarioDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionService {

    private final NotificacionRepository repository;
    private final EmailService emailService;

    public NotificacionService(NotificacionRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    @Transactional
    public Notificacion crear(UsuarioDTO usuarioDTO, TipoNotificacion tipo, String titulo, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioId(usuarioDTO.getId());
        notificacion.setTipo(tipo);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setFechaCreacion(LocalDateTime.now());
        notificacion.setLeida(false);
        notificacion.setEmailEnviado(false);

        Notificacion guardada = repository.save(notificacion);

        // Enviar email en segundo plano
        try {
            emailService.enviarEmail(usuarioDTO.getEmail(), titulo, mensaje);
            guardada.setEmailEnviado(true);
            repository.save(guardada);
        } catch (Exception e) {
            System.err.println("Error enviando email para notificación " + guardada.getId());
        }

        return guardada;
    }

    /**
     * Crea notificación sin enviar email (solo in-app)
     */
    @Transactional
    public Notificacion crearSoloInApp(UsuarioDTO usuarioDTO, TipoNotificacion tipo, String titulo, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioId(usuarioDTO.getId());
        notificacion.setTipo(tipo);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setEmailEnviado(false);

        return repository.save(notificacion);
    }

    /**
     * Marca una notificación como leída
     */
    @Transactional
    public void marcarComoLeida(Long id) {
        repository.findById(id).ifPresent(notificacion -> {
            notificacion.marcarComoLeida();
            repository.save(notificacion);
        });
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas
     */
    @Transactional
    public void marcarTodasComoLeidas(Long usuarioId) {
        List<Notificacion> noLeidas = repository.findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(usuarioId);
        noLeidas.forEach(n -> {
            n.marcarComoLeida();
            repository.save(n);
        });
    }

    /**
     * Obtiene todas las notificaciones de un usuario
     */
    public List<NotificacionDTO> obtenerPorUsuario(Long usuarioId) {
        return repository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene solo notificaciones no leídas
     */
    public List<NotificacionDTO> obtenerNoLeidas(Long usuarioId) {
        return repository.findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Cuenta notificaciones no leídas
     */
    public Long contarNoLeidas(Long usuarioId) {
        return repository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    /**
     * Obtiene notificaciones recientes (últimos 30 días)
     */
    public List<NotificacionDTO> obtenerRecientes(Long usuarioId) {
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        return repository.findRecentesByUsuarioId(usuarioId, hace30Dias)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Elimina notificaciones antiguas (más de 90 días)
     */
    @Transactional
    public void limpiarNotificacionesAntiguas() {
        LocalDateTime hace90Dias = LocalDateTime.now().minusDays(90);
        repository.deleteAntiguasAntesDe(hace90Dias);
    }

    /**
     * Convierte entidad a DTO
     */
    private NotificacionDTO convertirADTO(Notificacion notificacion) {
        return new NotificacionDTO(
                notificacion.getId(),
                notificacion.getTipo(),
                notificacion.getTitulo(),
                notificacion.getMensaje(),
                notificacion.getLeida(),
                notificacion.getFechaCreacion(),
                notificacion.getFechaLeida());
    }
}
