package com.gym.backend.Shared.Security;

import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioNotFoundException;
import com.gym.backend.Usuarios.Domain.UsuarioUseCase;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioUseCase usuarioUseCase;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            String email = jwtService.extractEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    var usuario = usuarioUseCase.obtenerPorEmail(email);

                    if (jwtService.isTokenValid(token)) {
                        if (!usuario.esActivo()) {
                            log.warn("Usuario inactivo intentando acceder: {}", email);
                            sendErrorResponse(response, "Usuario desactivado", HttpServletResponse.SC_FORBIDDEN);
                            return;
                        }

                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                usuario, null, jwtService.getAuthorities(usuario.getRol().name()));
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("Usuario autenticado: {}", email);
                    }
                } catch (UsuarioNotFoundException e) {
                    log.warn("Usuario no encontrado en JWT filter: {}", email);
                }
            }
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado para usuario: {}", e.getClaims().getSubject());
            sendErrorResponse(response, "Token expirado", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (JwtException e) {
            log.warn("Token JWT inválido: {}", e.getMessage());
            sendErrorResponse(response, "Token inválido", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (Exception e) {
            System.err.println("❌❌❌ ERROR CRÍTICO EN FILTRO JWT ❌❌❌");
            e.printStackTrace(); // Imprimir stacktrace completo en consola
            log.error("Error en filtro JWT", e);
            sendErrorResponse(response, "Error de autenticación: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}