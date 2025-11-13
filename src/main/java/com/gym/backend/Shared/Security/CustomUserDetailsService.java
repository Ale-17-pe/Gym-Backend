package com.gym.backend.Shared.Security;

import com.gym.backend.Usuarios.Infrastructure.Adapter.UsuarioRepositoryAdapter;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepositoryAdapter repo;

    public CustomUserDetailsService(UsuarioRepositoryAdapter repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        var usuario = repo.buscarPorEmail(email);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword()) // ya viene encriptado
                .roles(usuario.getRol())         // ADMIN, RECEPCIONISTA, CLIENTE
                .build();
    }
}
