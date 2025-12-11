package com.gym.backend.Shared.Scheduler;

import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import com.gym.backend.Membresias.Domain.*;
import com.gym.backend.Notificacion.Application.NotificacionService;
import com.gym.backend.Notificacion.Domain.TipoNotificacion;
import com.gym.backend.Planes.Application.Dto.PlanDTO;
import com.gym.backend.Planes.Application.Mapper.PlanMapper;
import com.gym.backend.Planes.Domain.*;
import com.gym.backend.Shared.Email.EmailService;
import com.gym.backend.Usuarios.Application.Dto.UsuarioDTO;
import com.gym.backend.Usuarios.Application.Mapper.UsuarioMapper;
import com.gym.backend.Usuarios.Domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@EnableScheduling
public class NotificacionScheduler {

    @Autowired
    private MembresiaRepositoryPort membresiaRepository;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioRepositoryPort usuarioRepository;

    @Autowired
    private PlanRepositoryPort planRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private PlanMapper planMapper;

    /**
     * Ejecuta verificación diaria a las 9:00 AM
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void verificarMembresiasYNotificar() {
        LocalDate hoy = LocalDate.now();

        System.out.println("🔔 Iniciando verificación de membresías: " + hoy);

        // 1. Verificar membresías próximas a vencer
        verificarProximasVencer();

        // 2. Verificar membresías vencidas
        verificarVencidas();

        System.out.println("✅ Verificación completada: " + hoy);
    }

    /**
     * Notifica membresías próximas a vencer
     */
    private void verificarProximasVencer() {
        List<Membresia> proximasVencer = membresiaRepository.listarPorVencer();

        for (Membresia membresia : proximasVencer) {
            try {
                Usuario usuarioDomain = usuarioRepository.buscarPorId(membresia.getUsuarioId()).orElse(null);
                if (usuarioDomain == null)
                    continue;
                UsuarioDTO usuario = usuarioMapper.toDTO(usuarioDomain);

                Plan planDomain = planRepository.buscarPorId(membresia.getPlanId()).orElse(null);
                if (planDomain == null)
                    continue;
                PlanDTO plan = planMapper.toDTO(planDomain);

                long diasRestantes = membresia.diasRestantes();

                // Solo notificar en días específicos (7, 3, 1)
                if (diasRestantes == 7 || diasRestantes == 3 || diasRestantes == 1) {
                    notificacionService.crear(
                            usuario,
                            TipoNotificacion.MEMBRESIA_PROXIMA_VENCER,
                            String.format("⏰ Tu membresía vence en %d día%s", diasRestantes,
                                    diasRestantes > 1 ? "s" : ""),
                            String.format("Tu membresía '%s' vence el %s. Renueva pronto para continuar entrenando.",
                                    plan.getNombrePlan(),
                                    membresia.getFechaFin()));
                }
            } catch (Exception e) {
                System.err.println("Error procesando membresía próxima a vencer: " + e.getMessage());
            }
        }

        System.out.println(String.format("📧 Verificadas %d membresías próximas a vencer", proximasVencer.size()));
    }

    /**
     * Notifica membresías vencidas
     */
    private void verificarVencidas() {
        List<Membresia> activas = membresiaRepository.listarPorEstado(EstadoMembresia.ACTIVA);
        int vencidas = 0;

        for (Membresia membresia : activas) {
            try {
                if (membresia.estaVencida()) {
                    Usuario usuarioDomain = usuarioRepository.buscarPorId(membresia.getUsuarioId()).orElse(null);
                    if (usuarioDomain == null)
                        continue;
                    UsuarioDTO usuario = usuarioMapper.toDTO(usuarioDomain);

                    Plan planDomain = planRepository.buscarPorId(membresia.getPlanId()).orElse(null);
                    if (planDomain == null)
                        continue;
                    PlanDTO plan = planMapper.toDTO(planDomain);

                    // Marcar como vencida
                    membresia.vencer();
                    membresiaRepository.actualizar(membresia);

                    // Crear notificación
                    notificacionService.crear(
                            usuario,
                            TipoNotificacion.MEMBRESIA_VENCIDA,
                            "❌ Tu membresía ha vencido",
                            String.format("Tu membresía '%s' venció el %s. Renueva ahora para recuperar el acceso.",
                                    plan.getNombrePlan(),
                                    membresia.getFechaFin()));

                    vencidas++;
                }
            } catch (Exception e) {
                System.err.println("Error procesando membresía vencida: " + e.getMessage());
            }
        }

        System.out.println(String.format("📧 %d membresías marcadas como vencidas", vencidas));
    }

    /**
     * Limpia notificaciones antiguas cada domingo a las 3:00 AM
     */
    @Scheduled(cron = "0 0 3 * * SUN")
    public void limpiarNotificacionesAntiguas() {
        notificacionService.limpiarNotificacionesAntiguas();
        System.out.println("🧹 Limpieza de notificaciones antiguas completada");
    }
}
