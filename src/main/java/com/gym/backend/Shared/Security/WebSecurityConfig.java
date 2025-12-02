package com.gym.backend.Shared.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configure(http))
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authz -> authz
                                                // Endpoints públicos
                                                .requestMatchers("/api/auth/**", "/api/health/**", "/api/info/**",
                                                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                                                .permitAll()
                                                // Permitir acceso público a planes (sin login) - GET only
                                                .requestMatchers("/api/planes/activos", "/api/planes/destacados")
                                                .permitAll()
                                                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/planes",
                                                                "/api/planes/*")
                                                .permitAll()

                                                .requestMatchers("/api/comprobantes/**")
                                                .hasAnyRole("RECEPCIONISTA", "ADMINISTRADOR")
                                                // Endpoints de administrador (usuarios y configuración)
                                                .requestMatchers("/api/usuarios/**", "/api/configuracion/**")
                                                .hasRole("ADMINISTRADOR")
                                                // Endpoints de recepcionista - buscar usuarios (solo GET)
                                                .requestMatchers(org.springframework.http.HttpMethod.GET,
                                                                "/api/usuarios/dni/**", "/api/usuarios/buscar/**")
                                                .hasAnyRole("RECEPCIONISTA", "ADMINISTRADOR")
                                                // Endpoints de administrador para planes (POST, PUT, DELETE)
                                                .requestMatchers(org.springframework.http.HttpMethod.POST,
                                                                "/api/planes/**")
                                                .hasRole("ADMINISTRADOR")
                                                .requestMatchers(org.springframework.http.HttpMethod.PUT,
                                                                "/api/clases/tipos/**")
                                                .hasRole("ADMINISTRADOR")
                                                .requestMatchers(org.springframework.http.HttpMethod.DELETE,
                                                                "/api/clases/tipos/**")
                                                .hasRole("ADMINISTRADOR")

                                                // Admin: Gestión de instructores y horarios
                                                .requestMatchers("/api/clases/instructores/**",
                                                                "/api/clases/horarios/**")
                                                .hasRole("ADMINISTRADOR")

                                                .requestMatchers("/api/asistencias/**")
                                                .hasAnyRole("RECEPCIONISTA", "ADMINISTRADOR")
                                                // Endpoints de reportes - incluye CONTADOR
                                                .requestMatchers("/api/reportes/**")
                                                .hasAnyRole("RECEPCIONISTA", "ADMINISTRADOR", "CONTADOR")

                                                // Admin: Generar sesiones
                                                .requestMatchers(org.springframework.http.HttpMethod.POST,
                                                                "/api/clases/sesiones/generar")
                                                .hasRole("ADMINISTRADOR")

                                                // Autenticados: Resto de clases (GET tipos, sesiones, reservas)
                                                .requestMatchers("/api/clases/**")
                                                .authenticated()

                                                // Endpoints que requieren autenticación (cualquier usuario logueado)
                                                .requestMatchers("/api/membresias/**", "/api/pagos/**",
                                                                "/api/perfil/**", "/api/notificaciones/**")
                                                .authenticated()
                                                // Cualquier otra petición requiere autenticación
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .formLogin(form -> form.disable())
                                .httpBasic(httpBasic -> httpBasic.disable());

                return http.build();
        }
}