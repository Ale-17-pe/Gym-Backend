package com.gym.backend.Shared.Security;

import com.gym.backend.Usuarios.Domain.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Usuario usuario) {
        try {
            return Jwts.builder()
                    .setSubject(usuario.getEmail())
                    .claim("id", usuario.getId())
                    .claim("nombre", usuario.getNombre())
                    .claim("apellido", usuario.getApellido())
                    .claim("rol", usuario.getRol().name())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("Error generando token JWT", e);
            throw new RuntimeException("Error generando token");
        }
    }

    public String extractEmail(String token) { return getClaims(token).getSubject(); }
    public Long extractUserId(String token) { return getClaims(token).get("id", Long.class); }
    public String extractRol(String token) { return getClaims(token).get("rol", String.class); }

    public boolean isTokenValid(String token) {
        try { getClaims(token); return true; }
        catch (JwtException | IllegalArgumentException e) {
            log.warn("Token JWT inválido: {}", e.getMessage()); return false;
        }
    }

    public LocalDateTime getExpirationFromToken(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Collection<? extends GrantedAuthority> getAuthorities(String rol) {
        String normalizedRol = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol;
        return List.of(new SimpleGrantedAuthority(normalizedRol));
    }
}
