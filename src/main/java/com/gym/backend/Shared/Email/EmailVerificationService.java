package com.gym.backend.Shared.Email;

import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Domain.UsuarioRepositoryPort;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de verificación de email para nuevos usuarios.
 * Envía un código de 6 dígitos al email del usuario que debe ser verificado
 * para activar completamente la cuenta.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final CacheManager cacheManager;
    private final EmailService emailService;
    private final UsuarioRepositoryPort usuarioRepository;
    private final com.gym.backend.Fidelidad.Application.PuntosFidelidadUseCase puntosFidelidadUseCase;
    private final SecureRandom random = new SecureRandom();

    // Cache de expiración de códigos (30 minutos)
    private final Map<String, LocalDateTime> codeExpirationMap = new ConcurrentHashMap<>();
    private static final int CODE_EXPIRATION_MINUTES = 30;
    private static final String CACHE_NAME = "email-verification-codes";

    /**
     * Genera y envía un código de verificación al email del usuario
     */
    public void sendVerificationCode(String email) {
        String code = String.format("%06d", random.nextInt(999999));

        // Guardar en cache
        Objects.requireNonNull(cacheManager.getCache(CACHE_NAME))
                .put(email, code);

        // Guardar expiración
        codeExpirationMap.put(email, LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES));

        // Enviar email
        emailService.sendEmailVerificationCode(email, code);

        log.info("📧 Código de verificación enviado a: {}", email);
    }

    /**
     * Valida el código de verificación ingresado por el usuario
     */
    public boolean validateCode(String email, String code) {
        var cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) {
            log.error("Cache {} no encontrado", CACHE_NAME);
            return false;
        }

        String storedCode = cache.get(email, String.class);

        if (storedCode == null || !storedCode.equals(code)) {
            log.warn("❌ Código de verificación inválido para: {}", email);
            return false;
        }

        LocalDateTime expiration = codeExpirationMap.get(email);
        if (expiration == null || LocalDateTime.now().isAfter(expiration)) {
            log.warn("⏰ Código de verificación expirado para: {}", email);
            invalidateCode(email);
            return false;
        }

        log.info("✅ Código de verificación válido para: {}", email);
        return true;
    }

    /**
     * Marca el email del usuario como verificado después de validar el código
     */
    public void markEmailAsVerified(String email) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado: " + email));

        usuario.verificarEmail();
        usuarioRepository.actualizar(usuario);

        // Limpiar código usado
        invalidateCode(email);

        // Otorgar puntos de fidelidad por verificar email
        try {
            puntosFidelidadUseCase.otorgarPuntos(
                    usuario.getId(),
                    com.gym.backend.Fidelidad.Domain.Enum.MotivoGanancia.VERIFICAR_EMAIL,
                    usuario.getId(),
                    "VERIFICACION_EMAIL");
            log.info("🎯 Puntos otorgados por verificación de email a: {}", email);
        } catch (Exception e) {
            log.warn("⚠️ No se pudieron otorgar puntos por verificación: {}", e.getMessage());
        }

        log.info("✅ Email verificado exitosamente para: {}", email);
    }

    /**
     * Invalida un código de verificación
     */
    public void invalidateCode(String email) {
        var cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.evict(email);
        }
        codeExpirationMap.remove(email);
    }

    /**
     * Reenvía el código de verificación
     */
    public void resendVerificationCode(String email) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado: " + email));

        if (usuario.tieneEmailVerificado()) {
            log.info("Email ya verificado para: {}", email);
            throw new IllegalStateException("El email ya está verificado");
        }

        // Invalidar código anterior y enviar nuevo
        invalidateCode(email);
        sendVerificationCode(email);
    }
}
