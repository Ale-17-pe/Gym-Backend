package com.gym.backend.Shared.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public WebSecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> cors.configure(http)); // CORS habilitado

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()

                // ADMIN ONLY
                .requestMatchers("/api/planes/**").hasRole("ADMIN")

                // RECEPCIÓN + ADMIN
                .requestMatchers("/api/asistencias/**").hasAnyRole("RECEPCIONISTA", "ADMIN")

                // CLIENTES (con token)
                .requestMatchers("/api/membresias/**", "/api/pagos/**").authenticated()

                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        http.httpBasic(httpBasic -> httpBasic.disable());
        http.formLogin(form -> form.disable());

        return http.build();
    }
}