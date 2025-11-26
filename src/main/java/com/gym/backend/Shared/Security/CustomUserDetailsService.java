package com.gym.backend.Shared.Security;

import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioNotFoundException;
import com.gym.backend.Usuarios.Domain.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioUseCase usuarioUseCase;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Buscando usuario por email: {}", email);

        try {
            var usuario = usuarioUseCase.obtenerPorEmail(email);

            if (!usuario.esActivo()) {
                log.warn("Usuario inactivo: {}", email);
                throw new UsernameNotFoundException("Usuario desactivado: " + email);
            }

            String rol = usuario.getRol().name().startsWith("ROLE_") ? usuario.getRol().name()
                    : "ROLE_" + usuario.getRol().name();

            return User.builder()
                    .username(usuario.getEmail())
                    .password(usuario.getPassword())
                    .authorities(rol)
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();

        } catch (UsuarioNotFoundException e) {
            log.warn("Usuario no encontrado: {}", email);
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }
    }
}