package com.gym.backend.Shared.Security;

import com.gym.backend.Usuarios.Infrastructure.Adapter.UsuarioRepositoryAdapter;
import io.jsonwebtoken.ExpiredJwtException;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter implements Filter {

    private final JwtService jwtService;
    private final UsuarioRepositoryAdapter usuarioRepo;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioRepositoryAdapter usuarioRepo) {
        this.jwtService = jwtService;
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) request;

        String header = http.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            String email = jwtService.extractEmail(token);

            var usuario = usuarioRepo.buscarPorEmail(email);
            if (usuario == null) {
                chain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            usuario,
                            null,
                            jwtService.getAuthorities(usuario.getRol())
                    );

            auth.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(http)
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (ExpiredJwtException ex) {
            HttpServletResponse res = (HttpServletResponse) response;
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }
}
