package com.gym.backend.Shared.Email;

import com.gym.backend.Comprobante.Application.Dto.DatosComprobante;
import com.gym.backend.Comprobante.Domain.Services.ComprobantePdfService;
import com.gym.backend.Pago.Application.Dto.PagoDTO;
import com.gym.backend.Pago.Application.Mapper.PagoMapper;
import com.gym.backend.Pago.Domain.Pago;
import com.gym.backend.Pago.Domain.PagoRepositoryPort;
import com.gym.backend.Planes.Application.Dto.PlanDTO;
import com.gym.backend.Planes.Application.Mapper.PlanMapper;
import com.gym.backend.Planes.Domain.Plan;
import com.gym.backend.Planes.Domain.PlanRepositoryPort;
import com.gym.backend.Qr.Domain.QrUseCase;
import com.gym.backend.Usuarios.Application.Dto.UsuarioDTO;
import com.gym.backend.Usuarios.Application.Mapper.UsuarioMapper;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Domain.UsuarioRepositoryPort;
import com.gym.backend.Membresias.Domain.Membresia;
import com.gym.backend.Membresias.Domain.MembresiaRepositoryPort;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;

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

    @Autowired
    private QrUseCase qrUseCase;

    @Autowired
    private ComprobantePdfService comprobantePdfService;

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
     * Envía comprobante de pago confirmado con QR embebido y PDF adjunto
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

            // Generar QR en base64
            String qrBase64 = "";
            try {
                byte[] qrBytes = qrUseCase.generarQRBytes(pago.getCodigoPago());
                qrBase64 = Base64.getEncoder().encodeToString(qrBytes);
            } catch (Exception qrError) {
                System.err.println("⚠️ No se pudo generar QR: " + qrError.getMessage());
            }

            String asunto = "💳 Comprobante de Pago - AresFitness";

            // Email con plantilla HTML incluyendo QR
            String contenidoHTML = EmailTemplates.getPaymentReceiptEmailTemplate(
                    usuario.getNombre() + " " + usuario.getApellido(),
                    plan.getNombrePlan(),
                    pago.getMonto(),
                    pago.getCodigoPago(),
                    qrBase64,
                    pago.getMetodoPago().toString());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(REMITENTE);
            helper.setTo(usuario.getEmail());
            helper.setSubject(asunto);
            helper.setText(contenidoHTML, true);

            // Generar y adjuntar PDF del comprobante
            try {
                DatosComprobante datosComprobante = DatosComprobante.builder()
                        .nombreGimnasio("AresFitness")
                        .direccionGimnasio("Av. Principal 123, Lima")
                        .rucGimnasio("20123456789")
                        .telefonoGimnasio("+51 999 888 777")
                        .numeroComprobante(String.format("CP-%06d", pagoId))
                        .nombreCliente(usuario.getNombre() + " " + usuario.getApellido())
                        .documentoCliente(usuario.getDni())
                        .conceptoPago("Membresía " + plan.getNombrePlan())
                        .subtotal(BigDecimal.valueOf(pago.getMonto()))
                        .igv(BigDecimal.valueOf(pago.getMonto() * 0.18))
                        .total(BigDecimal.valueOf(pago.getMonto() * 1.18))
                        .metodoPago(pago.getMetodoPago().toString())
                        .codigoPago(pago.getCodigoPago())
                        .fechaEmision(LocalDateTime.now())
                        .pagoId(pagoId)
                        .build();

                byte[] pdfBytes = comprobantePdfService.generarComprobantePDF(datosComprobante);
                helper.addAttachment("Comprobante_" + pago.getCodigoPago() + ".pdf",
                        new ByteArrayResource(pdfBytes));
                System.out.println("📄 PDF adjuntado al email");
            } catch (Exception pdfError) {
                System.err.println("⚠️ No se pudo adjuntar PDF: " + pdfError.getMessage());
                // Continuar enviando el email sin el PDF
            }

            mailSender.send(message);
            System.out.println("✅ Email de comprobante enviado a: " + usuario.getEmail());

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
     * Envía el código de verificación de email para nuevos usuarios
     */
    public void sendEmailVerificationCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(REMITENTE);
            helper.setTo(toEmail);
            helper.setSubject("📧 Verifica tu Email - AresFitness");

            String htmlContent = EmailTemplates.getEmailVerificationTemplate(code);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ Email de verificación enviado a: " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ Error enviando email de verificación: " + e.getMessage());
            displayCodeInConsole(toEmail, code);
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

    /**
     * Envía un email de felicitación de cumpleaños
     */
    public void enviarFelicitacionCumpleanos(Usuario usuario) {
        try {
            String asunto = "🎂 ¡Feliz Cumpleaños " + usuario.getNombre() + "! - AresFitness";
            String contenidoHTML = EmailTemplates.getBirthdayEmailTemplate(
                    usuario.getNombre(),
                    50); // Puntos de cumpleaños

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(REMITENTE);
            helper.setTo(usuario.getEmail());
            helper.setSubject(asunto);
            helper.setText(contenidoHTML, true);
            mailSender.send(message);

            System.out.println("🎂 Email de cumpleaños enviado a: " + usuario.getEmail());

        } catch (Exception e) {
            System.err.println("❌ Error enviando email de cumpleaños: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
