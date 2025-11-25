package com.gym.backend.Notificacion.Presentation;

import com.gym.backend.Notificacion.Application.NotificacionService;
import com.gym.backend.Notificacion.DTO.NotificacionDTO;
import com.gym.backend.Shared.Security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de notificaciones funciona correctamente 🔔");
    }

    @GetMapping("/mis-notificaciones")
    public ResponseEntity<List<NotificacionDTO>> obtenerMisNotificaciones(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remover "Bearer "
        Long usuarioId = jwtService.extractUserId(token);

        List<NotificacionDTO> notificaciones = notificacionService.obtenerPorUsuario(usuarioId);
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<List<NotificacionDTO>> obtenerNoLeidas(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long usuarioId = jwtService.extractUserId(token);

            System.out.println("🔔 Obteniendo notificaciones no leídas para usuario: " + usuarioId);

            List<NotificacionDTO> notificaciones = notificacionService.obtenerNoLeidas(usuarioId);

            System.out.println("✅ Notificaciones encontradas: " + notificaciones.size());

            return ResponseEntity.ok(notificaciones);
        } catch (Exception e) {
            System.err.println("❌ ERROR en /no-leidas:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/no-leidas/count")
    public ResponseEntity<Long> contarNoLeidas(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long usuarioId = jwtService.extractUserId(token);

            Long count = notificacionService.contarNoLeidas(usuarioId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Marca una notificación como leída
     */
    @PatchMapping("/{id}/marcar-leida")
    public ResponseEntity<Void> marcarLeida(
            @PathVariable Long id) {
        notificacionService.marcarComoLeida(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Marca todas las notificaciones como leídas
     */
    @PatchMapping("/marcar-todas-leidas")
    public ResponseEntity<Void> marcarTodasLeidas(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long usuarioId = jwtService.extractUserId(token);

        notificacionService.marcarTodasComoLeidas(usuarioId);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtiene notificaciones recientes (últimos 30 días)
     */
    @GetMapping("/recientes")
    public ResponseEntity<List<NotificacionDTO>> obtenerRecientes(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long usuarioId = jwtService.extractUserId(token);

        List<NotificacionDTO> notificaciones = notificacionService.obtenerRecientes(usuarioId);
        return ResponseEntity.ok(notificaciones);
    }
}
