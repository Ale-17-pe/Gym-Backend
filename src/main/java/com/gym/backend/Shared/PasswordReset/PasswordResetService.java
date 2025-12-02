package com.gym.backend.Shared.PasswordReset;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class PasswordResetService {

    private final CacheManager cacheManager;
    private final com.gym.backend.Shared.Email.EmailService emailService;
    private final SecureRandom random;
    private final Map<String, LocalDateTime> codeExpirationMap;

    public PasswordResetService(CacheManager cacheManager, com.gym.backend.Shared.Email.EmailService emailService) {
        this.cacheManager = cacheManager;
        this.emailService = emailService;
        this.random = new SecureRandom();
        this.codeExpirationMap = new HashMap<>();
    }

    public String generateResetCode(String email) {
        String code = String.format("%06d", random.nextInt(999999));

        Objects.requireNonNull(cacheManager.getCache("password-reset-codes"))
                .put(email, code);

        codeExpirationMap.put(email, LocalDateTime.now().plusMinutes(15));

        emailService.sendPasswordResetCode(email, code);

        log.info("Código de recuperación generado para: {}", email);
        return code;
    }

    public boolean validateResetCode(String email, String code) {
        var cache = cacheManager.getCache("password-reset-codes");
        if (cache == null) {
            return false;
        }

        String storedCode = cache.get(email, String.class);

        if (storedCode == null || !storedCode.equals(code)) {
            log.warn("Código de recuperación inválido para: {}", email);
            return false;
        }

        LocalDateTime expiration = codeExpirationMap.get(email);
        if (expiration == null || LocalDateTime.now().isAfter(expiration)) {
            log.warn("Código de recuperación expirado para: {}", email);
            invalidateCode(email);
            return false;
        }

        log.info("✅ Código de recuperación validado para: {}", email);
        return true;
    }

    public void invalidateCode(String email) {
        var cache = cacheManager.getCache("password-reset-codes");
        if (cache != null) {
            cache.evict(email);
        }
        codeExpirationMap.remove(email);
        log.info("Código de recuperación invalidado para: {}", email);
    }
}
