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
                                                // Endpoints de administrador (usuarios y configuración)
                                                .requestMatchers("/api/usuarios/**", "/api/configuracion/**")
                                                .hasRole("ADMINISTRADOR")
                                                // Endpoints de administrador para planes (POST, PUT, DELETE)
                                                .requestMatchers(org.springframework.http.HttpMethod.POST,
                                                                "/api/planes/**")
                                                .hasRole("ADMINISTRADOR")
                                                .requestMatchers(org.springframework.http.HttpMethod.PUT,
                                                                "/api/planes/**")
                                                .hasRole("ADMINISTRADOR")
                                                .requestMatchers(org.springframework.http.HttpMethod.DELETE,
                                                                "/api/planes/**")
                                                .hasRole("ADMINISTRADOR")
                                                // Endpoints de recepcionista y administrador
                                                .requestMatchers("/api/asistencias/**", "/api/reportes/**")
                                                .hasAnyRole("RECEPCIONISTA", "ADMINISTRADOR")
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