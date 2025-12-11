package com.gym.backend.Usuarios.Application;

import com.gym.backend.Usuarios.Domain.*;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio para acceder a datos de cliente de manera unificada
 * Provee métodos para obtener información del cliente desde usuarioId
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final UsuarioRepositoryPort usuarioRepo;
    private final PersonaRepositoryPort personaRepo;
    private final ClienteRepositoryPort clienteRepo;

    /**
     * Obtiene el Cliente por usuarioId
     */
    public Optional<Cliente> obtenerClientePorUsuarioId(Long usuarioId) {
        return clienteRepo.buscarPorUsuarioId(usuarioId);
    }

    /**
     * Obtiene el Cliente o lanza excepción
     */
    public Cliente obtenerClienteRequerido(Long usuarioId) {
        return clienteRepo.buscarPorUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado para usuario: " + usuarioId));
    }

    /**
     * Obtiene la Persona por usuarioId
     */
    public Optional<Persona> obtenerPersonaPorUsuarioId(Long usuarioId) {
        return personaRepo.buscarPorUsuarioId(usuarioId);
    }

    /**
     * Obtiene el nombre completo del usuario/cliente
     */
    public String obtenerNombreCompleto(Long usuarioId) {
        return personaRepo.buscarPorUsuarioId(usuarioId)
                .map(Persona::getNombreCompleto)
                .orElse("Usuario " + usuarioId);
    }

    /**
     * Verifica si un usuario es cliente activo
     */
    public boolean esClienteActivo(Long usuarioId) {
        return clienteRepo.buscarPorUsuarioId(usuarioId)
                .map(Cliente::getActivo)
                .orElse(false);
    }

    /**
     * Obtiene el Usuario con todos sus datos (Persona + Cliente)
     */
    public Usuario obtenerUsuarioCompleto(Long usuarioId) {
        return usuarioRepo.buscarPorIdConDatosCompletos(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
    }

    /**
     * Obtiene el clienteId a partir del usuarioId
     */
    public Long obtenerClienteId(Long usuarioId) {
        return clienteRepo.buscarPorUsuarioId(usuarioId)
                .map(Cliente::getId)
                .orElse(null);
    }

    /**
     * Actualiza datos del cliente
     */
    public Cliente actualizarCliente(Cliente cliente) {
        return clienteRepo.guardar(cliente);
    }
}
