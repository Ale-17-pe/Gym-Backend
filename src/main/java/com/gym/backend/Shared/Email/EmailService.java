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
import com.gym.backend.Membresias.Domain.Membresia;
import com.gym.backend.Membresias.Domain.MembresiaRepositoryPort;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Autowired
    private MembresiaRepositoryPort membresiaRepository;

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
        try {
            Pago pagoDomain = pagoRepository.buscarPorId(pagoId).orElse(null);
            if (pagoDomain == null) {
                System.err.println("❌ Pago no encontrado: " + pagoId);
                return;
            }
            PagoDTO pago = pagoMapper.toDTO(pagoDomain);

            Usuario usuarioDomain = usuarioRepository.buscarPorId(pago.getUsuarioId()).orElse(null);
            if (usuarioDomain == null) {
                System.err.println("❌ Usuario no encontrado: " + pago.getUsuarioId());
                return;
            }
            UsuarioDTO usuario = usuarioMapper.toDTO(usuarioDomain);

            Plan planDomain = planRepository.buscarPorId(pago.getPlanId()).orElse(null);
            if (planDomain == null) {
                System.err.println("❌ Plan no encontrado: " + pago.getPlanId());
                return;
            }
            PlanDTO plan = planMapper.toDTO(planDomain);

            String asunto = "💳 Código de Pago - AresFitness";

            // Email con plantilla HTML (sin QR por ahora)
            String contenidoHTML = String.format(
                    """
                            <!DOCTYPE html>
                            <html>
                            <body style="font-family: Arial; background: #0a0a0a; color: #fff; padding: 20px;">
                                <div style="max-width: 600px; margin: 0 auto; background: #1a1a1a; padding: 40px; border-radius: 10px;">
                                    <h1 style="color: #FFD500;">💳 Código de Pago Generado</h1>
                                    <p>Hola <strong>%s %s</strong>,</p>
                                    <p>Tu código de pago ha sido generado exitosamente:</p>

                                    <div style="background: #FFD500; color: #000; padding: 20px; text-align: center; border-radius: 8px; margin: 20px 0;">
                                        <h2 style="margin: 0; font-size: 32px; letter-spacing: 4px;">%s</h2>
                                    </div>

                                    <h3 style="color: #FFD500;">📋 Detalles:</h3>
                                    <ul style="line-height: 2;">
                                        <li><strong>Plan:</strong> %s</li>
                                        <li><strong>Monto:</strong> S/ %.2f</li>
                                        <li><strong>Método:</strong> %s</li>
                                    </ul>

                                    <p style="background: rgba(255,213,0,0.1); padding: 15px; border-left: 4px solid #FFD500;">
                                        ⚠️ <strong>Importante:</strong> Presenta este código en recepción para confirmar tu pago.
                                    </p>

                                    <p style="color: #888; font-size: 12px; margin-top: 30px;">
                                        © 2024 AresFitness - Tu mejor versión
                                    </p>
                                </div>
                            </body>
                            </html>
                            """,
                    usuario.getNombre(),
                    usuario.getApellido(),
                    pago.getCodigoPago(),
                    plan.getNombrePlan(),
                    pago.getMonto(),
                    pago.getMetodoPago());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(REMITENTE);
            helper.setTo(usuario.getEmail());
            helper.setSubject(asunto);
            helper.setText(contenidoHTML, true);
            mailSender.send(message);

            System.out.println("✅ Email de pago enviado a: " + usuario.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Error enviando email de pago: " + e.getMessage());
            e.printStackTrace();
        }
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

    /**
     * Envía el código 2FA por email con plantilla HTML profesional
     */
    public void send2FACode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(REMITENTE);
            helper.setTo(toEmail);
            helper.setSubject("🔐 Código de Verificación - AresFitness");

            String htmlContent = EmailTemplates.get2FAEmailTemplate(code);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ Email 2FA enviado a: " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ Error enviando email 2FA: " + e.getMessage());
            displayCodeInConsole(toEmail, code);
        }
    }

    /**
     * Envía el código de recuperación de contraseña por email
     */
    public void sendPasswordResetCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(REMITENTE);
            helper.setTo(toEmail);
            helper.setSubject("🔑 Recuperar Contraseña - AresFitness");

            String htmlContent = EmailTemplates.getPasswordRecoveryEmailTemplate(code);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ Email de recuperación enviado a: " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ Error enviando email de recuperación: " + e.getMessage());
        }
    }

    /**
     * Muestra el código en consola (fallback cuando falla el email)
     */
    private void displayCodeInConsole(String email, String code) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   CÓDIGO DE AUTENTICACIÓN 2FA          ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║   Usuario: " + email);
        System.out.println("║   Código:  " + code);
        System.out.println("║   Expira en 5 minutos                  ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    /**
     * Envía email de felicitaciones por membresía activada
     */
    public void enviarFelicitacionMembresia(Long pagoId) {
        try {
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

            Membresia membresia = membresiaRepository.buscarActivaPorUsuario(usuario.getId()).orElse(null);
            if (membresia == null) {
                // Si no se encuentra (raro porque se acaba de crear), usar fechas calculadas
                // O simplemente retornar/loggear error.
                // Para robustez, usaremos fechas del plan si no hay membresía activa (aunque
                // debería haber)
                System.err.println("⚠️ No se encontró membresía activa para usuario: " + usuario.getId());
                return;
            }

            String asunto = "🎉 ¡Membresía Activada! - AresFitness";
            String contenidoHTML = EmailTemplates.getMembershipActivatedEmailTemplate(
                    usuario.getNombre() + " " + usuario.getApellido(),
                    plan.getNombrePlan(),
                    membresia.getFechaInicio(),
                    membresia.getFechaFin());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(REMITENTE);
            helper.setTo(usuario.getEmail());
            helper.setSubject(asunto);
            helper.setText(contenidoHTML, true);
            mailSender.send(message);

            System.out.println("✅ Email de felicitaciones enviado a: " + usuario.getEmail());

        } catch (Exception e) {
            System.err.println("❌ Error enviando email de felicitaciones: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
