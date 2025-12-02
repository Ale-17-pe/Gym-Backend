package com.gym.backend.Shared.Email;

import java.time.LocalDate;

/**
 * Plantillas HTML para emails del sistema
 */
public class EmailTemplates {

    /**
     * Plantilla HTML profesional para código 2FA
     * Usa los colores de AresFitness: Dorado (#FFD500), Rojo (#FF0000), Negro
     * (#000000)
     */
    public static String get2FAEmailTemplate(String code) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Código de Verificación - AresFitness</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0a0a0a;">
                    <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="100%%" style="background-color: #0a0a0a;">
                        <tr>
                            <td style="padding: 40px 20px;">
                                <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="100%%" style="max-width: 600px; margin: 0 auto; background: linear-gradient(135deg, #1a1a1a 0%%, #0d0d0d 100%%); border-radius: 16px; overflow: hidden; box-shadow: 0 10px 40px rgba(255, 213, 0, 0.1);">

                                    <!-- Header con Logo -->
                                    <tr>
                                        <td style="background: linear-gradient(135deg, #FFD500 0%%, #FFA500 100%%); padding: 40px 30px; text-align: center;">
                                            <h1 style="margin: 0; color: #000000; font-size: 36px; font-weight: 800; letter-spacing: 2px; text-transform: uppercase;">
                                                ⚡ ARES<span style="color: #FF0000;">FITNESS</span>
                                            </h1>
                                            <p style="margin: 10px 0 0 0; color: #000000; font-size: 14px; font-weight: 600; letter-spacing: 1px;">
                                                TU MEJOR VERSIÓN
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Contenido Principal -->
                                    <tr>
                                        <td style="padding: 50px 40px; text-align: center;">
                                            <!-- Icono de Seguridad -->
                                            <div style="margin-bottom: 30px;">
                                                <div style="display: inline-block; background: linear-gradient(135deg, #FFD500 0%%, #FFA500 100%%); width: 80px; height: 80px; border-radius: 50%%; display: flex; align-items: center; justify-content: center; box-shadow: 0 8px 24px rgba(255, 213, 0, 0.3);">
                                                    <span style="font-size: 40px;">🔐</span>
                                                </div>
                                            </div>

                                            <h2 style="margin: 0 0 20px 0; color: #FFFFFF; font-size: 28px; font-weight: 700;">
                                                Código de Verificación
                                            </h2>

                                            <p style="margin: 0 0 40px 0; color: #CCCCCC; font-size: 16px; line-height: 1.6;">
                                                Hemos recibido una solicitud de acceso a tu cuenta de administrador.<br>
                                                Utiliza el siguiente código para completar tu inicio de sesión:
                                            </p>

                                            <!-- Código 2FA -->
                                            <div style="background: linear-gradient(135deg, #FFD500 0%%, #FFA500 100%%); border-radius: 12px; padding: 30px; margin: 0 auto 40px auto; max-width: 300px; box-shadow: 0 8px 24px rgba(255, 213, 0, 0.2);">
                                                <div style="font-size: 48px; font-weight: 800; color: #000000; letter-spacing: 8px; font-family: 'Courier New', monospace;">
                                                    %s
                                                </div>
                                            </div>

                                            <!-- Información de Expiración -->
                                            <div style="background: rgba(255, 0, 0, 0.1); border-left: 4px solid #FF0000; padding: 20px; margin: 0 0 30px 0; text-align: left; border-radius: 8px;">
                                                <p style="margin: 0; color: #FFFFFF; font-size: 14px; line-height: 1.6;">
                                                    ⏱️ <strong>Importante:</strong> Este código expirará en <strong style="color: #FFD500;">5 minutos</strong>.
                                                </p>
                                            </div>

                                            <!-- Advertencia de Seguridad -->
                                            <div style="background: rgba(255, 213, 0, 0.05); border: 1px solid rgba(255, 213, 0, 0.2); border-radius: 8px; padding: 20px; margin: 0 0 20px 0; text-align: left;">
                                                <p style="margin: 0 0 10px 0; color: #FFD500; font-size: 14px; font-weight: 600;">
                                                    🛡️ Seguridad
                                                </p>
                                                <p style="margin: 0; color: #CCCCCC; font-size: 13px; line-height: 1.6;">
                                                    Si no solicitaste este código, ignora este mensaje. Tu cuenta permanece segura.
                                                </p>
                                            </div>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background: #000000; padding: 30px 40px; text-align: center; border-top: 2px solid #FFD500;">
                                            <p style="margin: 0 0 15px 0; color: #FFFFFF; font-size: 16px; font-weight: 600;">
                                                💪 ¡Nos vemos en el gimnasio!
                                            </p>
                                            <p style="margin: 0 0 20px 0; color: #999999; font-size: 13px; line-height: 1.6;">
                                                Este es un mensaje automático del sistema de AresFitness.<br>
                                                Por favor, no respondas a este correo.
                                            </p>
                                            <div style="border-top: 1px solid #333333; padding-top: 20px; margin-top: 20px;">
                                                <p style="margin: 0; color: #666666; font-size: 12px;">
                                                    © 2024 AresFitness. Todos los derechos reservados.
                                                </p>
                                            </div>
                                        </td>
                                    </tr>

                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """
                .formatted(code);
    }

    /**
     * Plantilla HTML para email de bienvenida
     */
    public static String getWelcomeEmailTemplate(String nombre) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Bienvenido a AresFitness</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0a0a0a;">
                    <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="100%%" style="background-color: #0a0a0a;">
                        <tr>
                            <td style="padding: 40px 20px;">
                                <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="100%%" style="max-width: 600px; margin: 0 auto; background: linear-gradient(135deg, #1a1a1a 0%%, #0d0d0d 100%%); border-radius: 16px; overflow: hidden; box-shadow: 0 10px 40px rgba(255, 213, 0, 0.1);">

                                    <!-- Header -->
                                    <tr>
                                        <td style="background: linear-gradient(135deg, #FFD500 0%%, #FFA500 100%%); padding: 40px 30px; text-align: center;">
                                            <h1 style="margin: 0; color: #000000; font-size: 36px; font-weight: 800; letter-spacing: 2px; text-transform: uppercase;">
                                                ⚡ ARES<span style="color: #FF0000;">FITNESS</span>
                                            </h1>
                                        </td>
                                    </tr>

                                    <!-- Contenido -->
                                    <tr>
                                        <td style="padding: 50px 40px; text-align: center;">
                                            <h2 style="margin: 0 0 20px 0; color: #FFD500; font-size: 32px; font-weight: 700;">
                                                ¡Bienvenido, %s! 🎉
                                            </h2>
                                            <p style="margin: 0 0 30px 0; color: #CCCCCC; font-size: 16px; line-height: 1.6;">
                                                Estamos emocionados de tenerte en la familia AresFitness.<br>
                                                Tu camino hacia una vida más saludable comienza ahora.
                                            </p>

                                            <div style="background: rgba(255, 213, 0, 0.1); border-radius: 12px; padding: 30px; margin: 0 0 30px 0;">
                                                <h3 style="margin: 0 0 20px 0; color: #FFD500; font-size: 20px; font-weight: 600;">
                                                    🏋️ ¿Qué sigue?
                                                </h3>
                                                <ul style="margin: 0; padding: 0; list-style: none; text-align: left; color: #FFFFFF;">
                                                    <li style="margin-bottom: 15px; padding-left: 30px; position: relative;">
                                                        <span style="position: absolute; left: 0; color: #FFD500;">✓</span>
                                                        Activa tu membresía
                                                    </li>
                                                    <li style="margin-bottom: 15px; padding-left: 30px; position: relative;">
                                                        <span style="position: absolute; left: 0; color: #FFD500;">✓</span>
                                                        Conoce nuestras instalaciones
                                                    </li>
                                                    <li style="padding-left: 30px; position: relative;">
                                                        <span style="position: absolute; left: 0; color: #FFD500;">✓</span>
                                                        Consulta nuestros horarios
                                                    </li>
                                                </ul>
                                            </div>

                                            <p style="margin: 0; color: #FFFFFF; font-size: 18px; font-weight: 600;">
                                                💪 ¡Nos vemos pronto en el gimnasio!
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background: #000000; padding: 30px 40px; text-align: center; border-top: 2px solid #FFD500;">
                                            <p style="margin: 0; color: #666666; font-size: 12px;">
                                                © 2024 AresFitness. Todos los derechos reservados.
                                            </p>
                                        </td>
                                    </tr>

                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """
                .formatted(nombre);
    }

    /**
     * Plantilla HTML para recuperación de contraseña
     */
    public static String getPasswordRecoveryEmailTemplate(String resetCode) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Recuperar Contraseña - AresFitness</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0a0a0a;">
                    <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="100%%" style="background-color: #0a0a0a;">
                        <tr>
                            <td style="padding: 40px 20px;">
                                <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="100%%" style="max-width: 600px; margin: 0 auto; background: linear-gradient(135deg, #1a1a1a 0%%, #0d0d0d 100%%); border-radius: 16px; overflow: hidden; box-shadow: 0 10px 40px rgba(255, 213, 0, 0.1);">

                                    <!-- Header -->
                                    <tr>
                                        <td style="background: linear-gradient(135deg, #FFD500 0%%, #FFA500 100%%); padding: 40px 30px; text-align: center;">
                                            <h1 style="margin: 0; color: #000000; font-size: 36px; font-weight: 800; letter-spacing: 2px; text-transform: uppercase;">
                                                ⚡ ARES<span style="color: #FF0000;">FITNESS</span>
                                            </h1>
                                            <p style="margin: 10px 0 0 0; color: #000000; font-size: 14px; font-weight: 600; letter-spacing: 1px;">
                                                TU MEJOR VERSIÓN
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Contenido -->
                                    <tr>
                                        <td style="padding: 50px 40px; text-align: center;">
                                            <!-- Icono -->
                                            <div style="margin-bottom: 30px;">
                                                <div style="display: inline-block; background: linear-gradient(135deg, #FFD500 0%%, #FFA500 100%%); width: 80px; height: 80px; border-radius: 50%%; display: flex; align-items: center; justify-content: center; box-shadow: 0 8px 24px rgba(255, 213, 0, 0.3);">
                                                    <span style="font-size: 40px;">🔑</span>
                                                </div>
                                            </div>

                                            <h2 style="margin: 0 0 20px 0; color: #FFFFFF; font-size: 28px; font-weight: 700;">
                                                Recuperar Contraseña
                                            </h2>

                                            <p style="margin: 0 0 40px 0; color: #CCCCCC; font-size: 16px; line-height: 1.6;">
                                                Recibimos una solicitud para restablecer tu contraseña.<br>
                                                Usa el siguiente código para crear una nueva contraseña:
                                            </p>

                                            <!-- Código -->
                                            <div style="background: linear-gradient(135deg, #FFD500 0%%, #FFA500 100%%); border-radius: 12px; padding: 30px; margin: 0 auto 40px auto; max-width: 300px; box-shadow: 0 8px 24px rgba(255, 213, 0, 0.2);">
                                                <div style="font-size: 48px; font-weight: 800; color: #000000; letter-spacing: 8px; font-family: 'Courier New', monospace;">
                                                    %s
                                                </div>
                                            </div>

                                            <!-- Expiración -->
                                            <div style="background: rgba(255, 0, 0, 0.1); border-left: 4px solid #FF0000; padding: 20px; margin: 0 0 30px 0; text-align: left; border-radius: 8px;">
                                                <p style="margin: 0; color: #FFFFFF; font-size: 14px; line-height: 1.6;">
                                                    ⏱️ <strong>Importante:</strong> Este código expirará en <strong style="color: #FFD500;">15 minutos</strong>.
                                                </p>
                                            </div>

                                            <!-- Seguridad -->
                                            <div style="background: rgba(255, 213, 0, 0.05); border: 1px solid rgba(255, 213, 0, 0.2); border-radius: 8px; padding: 20px; margin: 0 0 20px 0; text-align: left;">
                                                <p style="margin: 0 0 10px 0; color: #FFD500; font-size: 14px; font-weight: 600;">
                                                    🛡️ Seguridad
                                                </p>
                                                <p style="margin: 0; color: #CCCCCC; font-size: 13px; line-height: 1.6;">
                                                    Si no solicitaste restablecer tu contraseña, ignora este mensaje. Tu cuenta permanece segura.
                                                </p>
                                            </div>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background: #000000; padding: 30px 40px; text-align: center; border-top: 2px solid #FFD500;">
                                            <p style="margin: 0 0 15px 0; color: #FFFFFF; font-size: 16px; font-weight: 600;">
                                                💪 ¡Nos vemos en el gimnasio!
                                            </p>
                                            <p style="margin: 0 0 20px 0; color: #999999; font-size: 13px; line-height: 1.6;">
                                                Este es un mensaje automático del sistema de AresFitness.<br>
                                                Por favor, no respondas a este correo.
                                            </p>
                                            <div style="border-top: 1px solid #333333; padding-top: 20px; margin-top: 20px;">
                                                <p style="margin: 0; color: #666666; font-size: 12px;">
                                                    © 2024 AresFitness. Todos los derechos reservados.
                                                </p>
                                            </div>
                                        </td>
                                    </tr>

                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """
                .formatted(resetCode);
    }

    /**
     * Plantilla HTML para comprobante de pago
     */
    public static String getPaymentReceiptEmailTemplate(String nombreUsuario, String planNombre,
            Double monto, String codigoPago,
            String qrCodeBase64, String metodoPago) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Comprobante de Pago - AresFitness</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0a0a0a;">
                    <table role="presentation" style="width: 100%%; background-color: #0a0a0a; padding: 40px 20px;">
                        <tr>
                            <td align="center">
                                <table role="presentation" style="max-width: 600px; width: 100%%; background: linear-gradient(135deg, #1a1a1a 0%%, #0a0a0a 100%%); border-radius: 20px; overflow: hidden; box-shadow: 0 20px 60px rgba(255, 213, 0, 0.3);">

                                    <!-- Header -->
                                    <tr>
                                        <td style="background: linear-gradient(135deg, #FFD500 0%%, #FFA500 100%%); padding: 40px; text-align: center;">
                                            <h1 style="margin: 0; color: #000000; font-size: 32px; font-weight: 800; text-transform: uppercase; letter-spacing: 2px;">
                                                ✅ Pago Confirmado
                                            </h1>
                                            <p style="margin: 10px 0 0 0; color: #000000; font-size: 14px; font-weight: 600;">
                                                COMPROBANTE DE PAGO
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Content -->
                                    <tr>
                                        <td style="padding: 40px;">

                                            <!-- Saludo -->
                                            <p style="margin: 0 0 30px 0; color: #FFFFFF; font-size: 18px; line-height: 1.6;">
                                                ¡Hola <strong style="color: #FFD500;">%s</strong>! 👋
                                            </p>

                                            <p style="margin: 0 0 30px 0; color: #CCCCCC; font-size: 16px; line-height: 1.6;">
                                                Tu pago ha sido <strong style="color: #00FF00;">confirmado exitosamente</strong>. A continuación encontrarás los detalles de tu compra:
                                            </p>

                                            <!-- Detalles del Pago -->
                                            <div style="background: rgba(255, 213, 0, 0.05); border: 2px solid #FFD500; border-radius: 12px; padding: 25px; margin: 0 0 30px 0;">
                                                <h2 style="margin: 0 0 20px 0; color: #FFD500; font-size: 20px; text-align: center;">
                                                    📋 Detalles del Pago
                                                </h2>

                                                <table style="width: 100%%; border-collapse: collapse;">
                                                    <tr>
                                                        <td style="padding: 12px 0; color: #AAAAAA; font-size: 14px; border-bottom: 1px solid rgba(255, 213, 0, 0.2);">
                                                            <strong>Plan:</strong>
                                                        </td>
                                                        <td style="padding: 12px 0; color: #FFFFFF; font-size: 14px; text-align: right; border-bottom: 1px solid rgba(255, 213, 0, 0.2);">
                                                            %s
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td style="padding: 12px 0; color: #AAAAAA; font-size: 14px; border-bottom: 1px solid rgba(255, 213, 0, 0.2);">
                                                            <strong>Monto:</strong>
                                                        </td>
                                                        <td style="padding: 12px 0; color: #00FF00; font-size: 18px; font-weight: bold; text-align: right; border-bottom: 1px solid rgba(255, 213, 0, 0.2);">
                                                            S/ %.2f
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td style="padding: 12px 0; color: #AAAAAA; font-size: 14px; border-bottom: 1px solid rgba(255, 213, 0, 0.2);">
                                                            <strong>Método de Pago:</strong>
                                                        </td>
                                                        <td style="padding: 12px 0; color: #FFFFFF; font-size: 14px; text-align: right; border-bottom: 1px solid rgba(255, 213, 0, 0.2);">
                                                            %s
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td style="padding: 12px 0; color: #AAAAAA; font-size: 14px;">
                                                            <strong>Código de Pago:</strong>
                                                        </td>
                                                        <td style="padding: 12px 0; color: #FFD500; font-size: 16px; font-weight: bold; text-align: right; font-family: 'Courier New', monospace;">
                                                            %s
                                                        </td>
                                                    </tr>
                                                </table>
                                            </div>

                                            <!-- Código QR -->
                                            <div style="background: #FFFFFF; border-radius: 12px; padding: 30px; margin: 0 0 30px 0; text-align: center;">
                                                <h3 style="margin: 0 0 20px 0; color: #000000; font-size: 18px;">
                                                    📱 Código QR de Pago
                                                </h3>
                                                <img src="data:image/png;base64,%s" alt="QR Code" style="max-width: 250px; width: 100%%; height: auto; border: 3px solid #FFD500; border-radius: 8px;">
                                                <p style="margin: 15px 0 0 0; color: #666666; font-size: 12px;">
                                                    Presenta este código QR en recepción
                                                </p>
                                            </div>

                                            <!-- Mensaje de Agradecimiento -->
                                            <div style="background: linear-gradient(135deg, rgba(255, 213, 0, 0.1) 0%%, rgba(255, 165, 0, 0.1) 100%%); border-radius: 12px; padding: 25px; text-align: center;">
                                                <p style="margin: 0; color: #FFFFFF; font-size: 16px; line-height: 1.6;">
                                                    🎉 <strong style="color: #FFD500;">¡Gracias por tu confianza!</strong><br>
                                                    Tu membresía ha sido activada exitosamente.
                                                </p>
                                                <p style="margin: 15px 0 0 0; color: #CCCCCC; font-size: 14px;">
                                                    💪 ¡Nos vemos en el gimnasio!
                                                </p>
                                            </div>

                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background: #000000; padding: 30px 40px; text-align: center; border-top: 2px solid #FFD500;">
                                            <p style="margin: 0; color: #666666; font-size: 12px;">
                                                © 2024 AresFitness. Todos los derechos reservados.
                                            </p>
                                            <p style="margin: 10px 0 0 0; color: #666666; font-size: 11px;">
                                                Este es un correo automático, por favor no responder.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """
                .formatted(nombreUsuario, planNombre, monto, metodoPago, codigoPago, qrCodeBase64);
    }

    /**
     * Plantilla HTML para felicitaciones por membresía activada
     */
    public static String getMembershipActivatedEmailTemplate(String nombreUsuario, String planNombre,
            LocalDate fechaInicio, LocalDate fechaFin) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>¡Membresía Activada! - AresFitness</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0a0a0a;">
                    <table role="presentation" style="width: 100%%; background-color: #0a0a0a; padding: 40px 20px;">
                        <tr>
                            <td align="center">
                                <table role="presentation" style="max-width: 600px; width: 100%%; background: linear-gradient(135deg, #1a1a1a 0%%, #0a0a0a 100%%); border-radius: 20px; overflow: hidden; box-shadow: 0 20px 60px rgba(255, 213, 0, 0.3);">

                                    <!-- Header -->
                                    <tr>
                                        <td style="background: linear-gradient(135deg, #FFD500 0%%, #FFA500 100%%); padding: 40px; text-align: center;">
                                            <h1 style="margin: 0; color: #000000; font-size: 28px; font-weight: 800; text-transform: uppercase; letter-spacing: 2px;">
                                                🎉 ¡Felicidades!
                                            </h1>
                                            <p style="margin: 10px 0 0 0; color: #000000; font-size: 14px; font-weight: 600;">
                                                MEMBRESÍA ACTIVADA
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Content -->
                                    <tr>
                                        <td style="padding: 40px;">

                                            <p style="margin: 0 0 30px 0; color: #FFFFFF; font-size: 18px; line-height: 1.6; text-align: center;">
                                                Hola <strong style="color: #FFD500;">%s</strong>,<br>
                                                ¡Tu membresía ha sido activada correctamente! 🚀
                                            </p>

                                            <!-- Detalles de la Membresía -->
                                            <div style="background: rgba(255, 213, 0, 0.05); border: 2px solid #FFD500; border-radius: 12px; padding: 25px; margin: 0 0 30px 0;">
                                                <h2 style="margin: 0 0 20px 0; color: #FFD500; font-size: 20px; text-align: center;">
                                                    🎫 Tu Plan: %s
                                                </h2>

                                                <table style="width: 100%%; border-collapse: collapse;">
                                                    <tr>
                                                        <td style="padding: 12px 0; color: #AAAAAA; font-size: 14px; border-bottom: 1px solid rgba(255, 213, 0, 0.2);">
                                                            <strong>📅 Inicio:</strong>
                                                        </td>
                                                        <td style="padding: 12px 0; color: #FFFFFF; font-size: 14px; text-align: right; border-bottom: 1px solid rgba(255, 213, 0, 0.2);">
                                                            %s
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td style="padding: 12px 0; color: #AAAAAA; font-size: 14px;">
                                                            <strong>🏁 Fin:</strong>
                                                        </td>
                                                        <td style="padding: 12px 0; color: #FFFFFF; font-size: 14px; text-align: right;">
                                                            %s
                                                        </td>
                                                    </tr>
                                                </table>
                                            </div>

                                            <p style="margin: 0 0 30px 0; color: #CCCCCC; font-size: 16px; line-height: 1.6; text-align: center;">
                                                Ahora eres parte de la élite. Prepárate para superar tus límites y alcanzar tu mejor versión.
                                            </p>

                                            <!-- Botón (Simulado) -->
                                            <div style="text-align: center; margin-bottom: 30px;">
                                                <a href="#" style="background-color: #FFD500; color: #000000; padding: 15px 30px; text-decoration: none; font-weight: bold; border-radius: 30px; display: inline-block; transition: all 0.3s ease;">
                                                    ACCEDER A MI CUENTA
                                                </a>
                                            </div>

                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background: #000000; padding: 30px 40px; text-align: center; border-top: 2px solid #FFD500;">
                                            <p style="margin: 0; color: #666666; font-size: 12px;">
                                                © 2024 AresFitness. Todos los derechos reservados.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """
                .formatted(nombreUsuario, planNombre, fechaInicio, fechaFin);
    }
}
