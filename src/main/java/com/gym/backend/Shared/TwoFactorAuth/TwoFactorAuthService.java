package com.gym.backend.Shared.TwoFactorAuth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Servicio para autenticación de dos factores (2FA)
 * Usa Spring Cache para almacenar códigos temporalmente (5 minutos)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TwoFactorAuthService {

    private final CacheManager cacheManager;
    private final com.gym.backend.Shared.Email.EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    // Almacena metadata adicional (timestamp de expiración)
    private final Map<String, LocalDateTime> codeExpirationMap = new HashMap<>();

    /**
     * Genera un código de 6 dígitos y lo almacena en caché
     * 
     * @param email Email del usuario
     * @return Código generado
     */
    public String generateCode(String email) {
        String code = String.format("%06d", random.nextInt(999999));

        // Guardar en caché
        Objects.requireNonNull(cacheManager.getCache("two-factor-codes"))
                .put(email, code);

        // Guardar tiempo de expiración (5 minutos)
        codeExpirationMap.put(email, LocalDateTime.now().plusMinutes(5));

        // Enviar por email (con fallback a consola si falla)
        emailService.send2FACode(email, code);

        return code;
    }

    /**
     * Valida el código ingresado por el usuario
     * 
     * @param email Email del usuario
     * @param code  Código a validar
     * @return true si el código es válido y no ha expirado
     */
    public boolean validateCode(String email, String code) {
        // Verificar si el código existe en caché
        var cache = cacheManager.getCache("two-factor-codes");
        if (cache == null) {
            return false;
        }

        String storedCode = cache.get(email, String.class);

        if (storedCode == null || !storedCode.equals(code)) {
            log.warn("Código 2FA inválido para: {}", email);
            return false;
        }

        // Verificar expiración
        LocalDateTime expiration = codeExpirationMap.get(email);
        if (expiration == null || LocalDateTime.now().isAfter(expiration)) {
            log.warn("Código 2FA expirado para: {}", email);
            invalidateCode(email);
            return false;
        }

        // Código válido - eliminar de caché (uso único)
        invalidateCode(email);
        log.info("✅ Código 2FA validado correctamente para: {}", email);

        return true;
    }

    /**
     * Invalida el código (elimina de caché)
     * 
     * @param email Email del usuario
     */
    public void invalidateCode(String email) {
        var cache = cacheManager.getCache("two-factor-codes");
        if (cache != null) {
            cache.evict(email);
        }
        codeExpirationMap.remove(email);
    }
}
