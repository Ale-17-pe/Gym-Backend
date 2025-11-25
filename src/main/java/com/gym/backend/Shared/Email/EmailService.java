package com.gym.backend.Shared.Email;

import com.gym.backend.Pago.Application.Dto.PagoDTO;
import com.gym.backend.Pago.Application.Mapper.PagoMapper;
import com.gym.backend.Pago.Domain.Pago;
import com.gym.backend.Pago.Domain.PagoRepositoryPort;
import com.gym.backend.Planes.Application.Dto.PlanDTO;
import com.gym.backend.Planes.Application.Mapper.PlanMapper;
import com.gym.backend.Planes.Domain.Plan;
import com.gym.backend.Planes.Domain.PlanRepositoryPort;
import com.gym.backend.Usuarios.Application.Dto.UsuarioDTO;
import com.gym.backend.Usuarios.Application.Mapper.UsuarioMapper;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Domain.UsuarioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UsuarioRepositoryPort usuarioRepository;

    @Autowired
    private PlanRepositoryPort planRepository;

    @Autowired
    private PagoRepositoryPort pagoRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private PlanMapper planMapper;

    @Autowired
    private PagoMapper pagoMapper;

    private static final String REMITENTE = "AresFitness <noreply@aresfitness.com>";

    /**
     * Envía un email simple
     */
    public void enviarEmail(String destinatario, String asunto, String contenido) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(REMITENTE);
            mensaje.setTo(destinatario);
            mensaje.setSubject(asunto);
            mensaje.setText(contenido);
            mailSender.send(mensaje);
        } catch (Exception e) {
            System.err.println("Error al enviar email a " + destinatario + ": " + e.getMessage());
        }
    }

    /**
     * Envía comprobante de pago confirmado
     */
    public void enviarComprobantePago(Long pagoId) {
        Pago pagoDomain = pagoRepository.buscarPorId(pagoId).orElse(null);
        if (pagoDomain == null)
            return;
        PagoDTO pago = pagoMapper.toDTO(pagoDomain);

        Usuario usuarioDomain = usuarioRepository.buscarPorId(pago.getUsuarioId()).orElse(null);
        if (usuarioDomain == null)
            return;
        UsuarioDTO usuario = usuarioMapper.toDTO(usuarioDomain);

        Plan planDomain = planRepository.buscarPorId(pago.getPlanId()).orElse(null);
        if (planDomain == null)
            return;
        PlanDTO plan = planMapper.toDTO(planDomain);

        String asunto = "✅ Pago Confirmado - AresFitness";

        String contenido = String.format("""
                Hola %s %s,

                ¡Tu pago ha sido confirmado exitosamente!

                📋 DETALLES DEL PAGO:
                • Código de Pago: %s
                • Monto: S/ %.2f
                • Fecha de Pago: %s
                • Plan: %s

                Gracias por confiar en AresFitness. ¡Nos vemos en el gimnasio! 💪

                ---
                AresFitness - Tu mejor versión
                """,
                usuario.getNombre(),
                usuario.getApellido(),
                pago.getCodigoPago(),
                pago.getMonto(),
                pago.getFechaPago().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                plan.getNombrePlan());

        enviarEmail(usuario.getEmail(), asunto, contenido);
    }

    /**
     * Envía email de bienvenida
     */
    public void enviarEmailBienvenida(Long usuarioId) {
        Usuario usuarioDomain = usuarioRepository.buscarPorId(usuarioId).orElse(null);
        if (usuarioDomain == null)
            return;
        UsuarioDTO usuario = usuarioMapper.toDTO(usuarioDomain);

        String asunto = "🎉 ¡Bienvenido a AresFitness!";

        String contenido = String.format(
                """
                        ¡Hola %s!

                        Bienvenido a la familia AresFitness. Estamos emocionados de acompañarte en tu camino hacia una vida más saludable.

                        🏋️ ¿QUÉ SIGUE?
                        • Activa tu membresía
                        • Conoce nuestras instalaciones
                        • Consulta nuestros horarios

                        ¡Nos vemos pronto en el gimnasio! 💪

                        ---
                        AresFitness - Tu mejor versión
                        """,
                usuario.getNombre());

        enviarEmail(usuario.getEmail(), asunto, contenido);
    }
}
