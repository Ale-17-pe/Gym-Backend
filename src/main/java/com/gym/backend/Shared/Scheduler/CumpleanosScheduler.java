package com.gym.backend.Shared.Scheduler;

import com.gym.backend.Fidelidad.Application.PuntosFidelidadUseCase;
import com.gym.backend.Fidelidad.Domain.Enum.MotivoGanancia;
import com.gym.backend.Notificacion.Application.NotificacionService;
import com.gym.backend.Notificacion.Domain.TipoNotificacion;
import com.gym.backend.Shared.Email.EmailService;
import com.gym.backend.Usuarios.Application.Dto.UsuarioDTO;
import com.gym.backend.Usuarios.Application.Mapper.UsuarioMapper;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Domain.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;

/**
 * Scheduler para enviar felicitaciones de cumpleaños automáticamente
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CumpleanosScheduler {

    private final UsuarioRepositoryPort usuarioRepository;
    private final EmailService emailService;
    private final NotificacionService notificacionService;
    private final PuntosFidelidadUseCase puntosFidelidadUseCase;
    private final UsuarioMapper usuarioMapper;

    private static final int PUNTOS_CUMPLEANOS = 50;

    /**
     * Ejecuta verificación diaria a las 8:00 AM para cumpleaños
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void verificarCumpleanos() {
        log.info("🎂 Verificando cumpleaños del día...");

        LocalDate hoy = LocalDate.now();
        MonthDay hoyMesYDia = MonthDay.from(hoy);

        List<Usuario> usuarios = usuarioRepository.listar();
        int cumpleaneros = 0;

        for (Usuario usuario : usuarios) {
            try {
                if (usuario.getFechaNacimiento() != null) {
                    MonthDay cumple = MonthDay.from(usuario.getFechaNacimiento());

                    if (cumple.equals(hoyMesYDia)) {
                        procesarCumpleanos(usuario);
                        cumpleaneros++;
                    }
                }
            } catch (Exception e) {
                log.error("Error procesando cumpleaños de usuario {}: {}", usuario.getId(), e.getMessage());
            }
        }

        log.info("🎂 {} cumpleaños procesados hoy", cumpleaneros);
    }

    /**
     * Procesa el cumpleaños de un usuario: envía email, notificación y puntos
     */
    private void procesarCumpleanos(Usuario usuario) {
        log.info("🎉 ¡Feliz cumpleaños a {}!", usuario.getNombreCompleto());

        // 1. Enviar email de cumpleaños
        try {
            emailService.enviarFelicitacionCumpleanos(usuario);
            log.info("📧 Email de cumpleaños enviado a: {}", usuario.getEmail());
        } catch (Exception e) {
            log.warn("⚠️ No se pudo enviar email de cumpleaños: {}", e.getMessage());
        }

        // 2. Crear notificación in-app
        try {
            UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuario);
            notificacionService.crearSoloInApp(
                    usuarioDTO,
                    TipoNotificacion.GENERAL,
                    "🎂 ¡Feliz Cumpleaños!",
                    String.format("¡%s, te deseamos un muy feliz cumpleaños! " +
                            "Como regalo, te hemos otorgado %d puntos de fidelidad. " +
                            "¡Disfruta tu día especial!",
                            usuario.getNombre(), PUNTOS_CUMPLEANOS));
        } catch (Exception e) {
            log.warn("⚠️ No se pudo crear notificación de cumpleaños: {}", e.getMessage());
        }

        // 3. Otorgar puntos de cumpleaños
        try {
            puntosFidelidadUseCase.otorgarPuntos(
                    usuario.getId(),
                    MotivoGanancia.CUMPLEANOS,
                    PUNTOS_CUMPLEANOS,
                    "¡Feliz Cumpleaños! Regalo especial",
                    usuario.getId(),
                    "CUMPLEANOS");
            log.info("🎁 {} puntos de cumpleaños otorgados a usuario {}", PUNTOS_CUMPLEANOS, usuario.getId());
        } catch (Exception e) {
            log.warn("⚠️ No se pudieron otorgar puntos de cumpleaños: {}", e.getMessage());
        }
    }
}
