package com.gym.backend.Auth.Infrastructure;

import com.gym.backend.Auth.Domain.AuthResponse;
import com.gym.backend.Auth.Domain.AuthServicePort;
import com.gym.backend.Auth.Domain.LoginCommand;
import com.gym.backend.Auth.Domain.RegisterCommand;
import com.gym.backend.Shared.Security.JwtService;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Infrastructure.Adapter.UsuarioRepositoryAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceAdapter implements AuthServicePort {

    private final UsuarioRepositoryAdapter usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceAdapter(
            UsuarioRepositoryAdapter usuarioRepo,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse login(LoginCommand command) {

        var usuario = usuarioRepo.buscarPorEmail(command.email());

        if (usuario == null)
            throw new IllegalStateException("Email o contraseña incorrectos");

        if (!passwordEncoder.matches(command.password(), usuario.getPassword()))
            throw new IllegalStateException("Email o contraseña incorrectos");

        String token = jwtService.generateToken(usuario);

        return new AuthResponse(token, usuario.getId(), usuario.getRol());
    }

    @Override
    public AuthResponse registrar(RegisterCommand command) {

        if (usuarioRepo.buscarPorEmail(command.email()) != null)
            throw new IllegalStateException("Email ya registrado");

        if (usuarioRepo.buscarPorDni(command.dni()) != null)
            throw new IllegalStateException("DNI ya registrado");

        String rol = command.rol();
        if (rol == null || rol.isBlank()) {
            rol = "CLIENTE"; // por defecto
        } else {
            rol = rol.toUpperCase();
            if (!rol.equals("ADMIN") && !rol.equals("RECEPCIONISTA") && !rol.equals("CLIENTE")) {
                rol = "CLIENTE"; // si envían basura
            }
        }

        var usuario = usuarioRepo.guardar(
                Usuario.builder()
                        .id(null)
                        .nombre(command.nombre())
                        .apellido(command.apellido())
                        .email(command.email())
                        .dni(command.dni())
                        .telefono(command.telefono())
                        .direccion(command.direccion())
                        .password(passwordEncoder.encode(command.password()))
                        .rol(rol)
                        .build()
        );

        String token = jwtService.generateToken(usuario);

        return new AuthResponse(token, usuario.getId(), usuario.getRol());
    }
}