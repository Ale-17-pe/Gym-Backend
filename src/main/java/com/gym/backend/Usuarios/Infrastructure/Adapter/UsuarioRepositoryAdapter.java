package com.gym.backend.Usuarios.Infrastructure.Adapter;

import com.gym.backend.Usuarios.Domain.*;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Infrastructure.Entity.*;
import com.gym.backend.Usuarios.Infrastructure.Jpa.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador del repositorio de Usuario (normalizado)
 * Maneja la creación de Usuario + Persona + Cliente/Empleado
 */
@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpa;
    private final PersonaJpaRepository personaJpa;
    private final ClienteJpaRepository clienteJpa;
    private final EmpleadoJpaRepository empleadoJpa;

    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = toEntity(usuario);
        UsuarioEntity guardado = usuarioJpa.save(entity);
        return toDomain(guardado);
    }

    @Override
    @Transactional
    public Usuario actualizar(Usuario usuario) {
        return usuarioJpa.findById(usuario.getId())
                .map(existente -> {
                    existente.setEmail(usuario.getEmail());
                    if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                        existente.setPassword(usuario.getPassword());
                    }
                    existente.setActivo(usuario.getActivo());
                    existente.setEmailVerificado(usuario.getEmailVerificado());
                    if (usuario.getRoles() != null) {
                        existente.setRoles(new HashSet<>(usuario.getRoles()));
                    }
                    return toDomain(usuarioJpa.save(existente));
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para actualizar"));
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioJpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioJpa.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorDni(String dni) {
        return personaJpa.findByDni(dni)
                .map(persona -> usuarioJpa.findById(persona.getUsuario().getId()).orElse(null))
                .map(this::toDomain);
    }

    @Override
    public List<Usuario> listar() {
        return usuarioJpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Usuario> listarPaginated(Pageable pageable) {
        return usuarioJpa.findAll(pageable).map(this::toDomain);
    }

    @Override
    public List<Usuario> listarPorRol(Rol rol) {
        return usuarioJpa.findByRol(rol).stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Usuario> listarPorRolPaginated(Rol rol, Pageable pageable) {
        return usuarioJpa.findByRolPaginated(rol, pageable).map(this::toDomain);
    }

    @Override
    public List<Usuario> listarPorActivo(Boolean activo) {
        return usuarioJpa.findByActivo(activo).stream().map(this::toDomain).toList();
    }

    @Override
    public void eliminar(Long id) {
        usuarioJpa.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorIdConDatosCompletos(Long id) {
        return usuarioJpa.findById(id).map(this::toDomainConDatosCompletos);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmailConDatosCompletos(String email) {
        return usuarioJpa.findByEmail(email).map(this::toDomainConDatosCompletos);
    }

    // ==================== Mappers ====================

    private Usuario toDomain(UsuarioEntity entity) {
        if (entity == null)
            return null;

        return Usuario.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .activo(entity.getActivo())
                .emailVerificado(entity.getEmailVerificado())
                .ultimoLogin(entity.getUltimoLogin())
                .roles(entity.getRoles() != null ? new HashSet<>(entity.getRoles()) : new HashSet<>())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private Usuario toDomainConDatosCompletos(UsuarioEntity entity) {
        if (entity == null)
            return null;

        Usuario usuario = toDomain(entity);

        // Cargar Persona
        if (entity.getPersona() != null) {
            usuario.setPersona(toPersonaDomain(entity.getPersona()));

            // Cargar Cliente si existe
            if (entity.getPersona().getCliente() != null) {
                usuario.setCliente(toClienteDomain(entity.getPersona().getCliente()));
            }

            // Cargar Empleado si existe
            if (entity.getPersona().getEmpleado() != null) {
                usuario.setEmpleado(toEmpleadoDomain(entity.getPersona().getEmpleado()));
            }
        }

        return usuario;
    }

    private Persona toPersonaDomain(PersonaEntity entity) {
        if (entity == null)
            return null;

        return Persona.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuario().getId())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .dni(entity.getDni())
                .genero(entity.getGenero())
                .fechaNacimiento(entity.getFechaNacimiento())
                .telefono(entity.getTelefono())
                .direccion(entity.getDireccion())
                .fotoPerfilUrl(entity.getFotoPerfilUrl())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private Cliente toClienteDomain(ClienteEntity entity) {
        if (entity == null)
            return null;

        return Cliente.builder()
                .id(entity.getId())
                .personaId(entity.getPersona().getId())
                .usuarioId(entity.getUsuarioId())
                .objetivoFitness(entity.getObjetivoFitness())
                .nivelExperiencia(entity.getNivelExperiencia())
                .condicionesMedicas(entity.getCondicionesMedicas())
                .contactoEmergenciaNombre(entity.getContactoEmergenciaNombre())
                .contactoEmergenciaTelefono(entity.getContactoEmergenciaTelefono())
                .comoNosConocio(entity.getComoNosConocio())
                .codigoReferido(entity.getCodigoReferido())
                .activo(entity.getActivo())
                .fechaRegistroGym(entity.getFechaRegistroGym())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private Empleado toEmpleadoDomain(EmpleadoEntity entity) {
        if (entity == null)
            return null;

        return Empleado.builder()
                .id(entity.getId())
                .personaId(entity.getPersona().getId())
                .usuarioId(entity.getUsuarioId())
                .codigoEmpleado(entity.getCodigoEmpleado())
                .fechaContratacion(entity.getFechaContratacion())
                .salario(entity.getSalario())
                .turno(entity.getTurno())
                .horaEntrada(entity.getHoraEntrada())
                .horaSalida(entity.getHoraSalida())
                .tipoContrato(entity.getTipoContrato())
                .activo(entity.getActivo())
                .fechaBaja(entity.getFechaBaja())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private UsuarioEntity toEntity(Usuario domain) {
        return UsuarioEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .activo(domain.getActivo() != null ? domain.getActivo() : true)
                .emailVerificado(domain.getEmailVerificado() != null ? domain.getEmailVerificado() : false)
                .ultimoLogin(domain.getUltimoLogin())
                .roles(domain.getRoles() != null ? new HashSet<>(domain.getRoles()) : new HashSet<>())
                .fechaCreacion(domain.getFechaCreacion())
                .fechaActualizacion(domain.getFechaActualizacion())
                .build();
    }
}