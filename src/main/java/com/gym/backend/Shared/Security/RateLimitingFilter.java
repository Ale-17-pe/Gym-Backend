package com.gym.backend.Shared.Security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro de Rate Limiting para proteger la API contra ataques de fuerza bruta.
 * 
 * Configuración:
 * - 100 requests por minuto por IP para endpoints normales
 * - 10 requests por minuto por IP para login (más restrictivo)
 * 
 * Esto previene:
 * - Ataques de fuerza bruta a login
 * - DDoS a nivel de aplicación
 * - Abuso de la API
 */
@Component
@Order(1)
public class RateLimitingFilter implements Filter {

    // Cache de buckets por IP
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();

    // Límites configurables
    private static final int REQUESTS_PER_MINUTE = 100;
    private static final int LOGIN_REQUESTS_PER_MINUTE = 10;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = getClientIP(httpRequest);
        String path = httpRequest.getRequestURI();

        // Obtener el bucket apropiado
        Bucket bucket = isLoginEndpoint(path)
                ? getLoginBucket(clientIp)
                : getBucket(clientIp);

        if (bucket.tryConsume(1)) {
            // Agregar headers de rate limit info
            httpResponse.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(bucket.getAvailableTokens()));
            chain.doFilter(request, response);
        } else {
            // Rate limit excedido
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    "{\"error\":\"Rate limit exceeded\",\"message\":\"Too many requests. Please try again later.\"}");
        }
    }

    private Bucket getBucket(String clientIp) {
        return buckets.computeIfAbsent(clientIp, k -> createBucket(REQUESTS_PER_MINUTE));
    }

    private Bucket getLoginBucket(String clientIp) {
        return loginBuckets.computeIfAbsent(clientIp, k -> createBucket(LOGIN_REQUESTS_PER_MINUTE));
    }

    private Bucket createBucket(int requestsPerMinute) {
        Bandwidth limit = Bandwidth.classic(requestsPerMinute,
                Refill.greedy(requestsPerMinute, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    private boolean isLoginEndpoint(String path) {
        return path.contains("/api/auth/login") || path.contains("/api/auth/register");
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
