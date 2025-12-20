package com.gym.backend.Usuarios.Application;

import com.gym.backend.Usuarios.Application.Dto.EntrenadorResponse;
import com.gym.backend.Usuarios.Domain.Enum.EspecialidadEntrenador;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Infrastructure.Entity.EmpleadoEntity;
import com.gym.backend.Usuarios.Infrastructure.Entity.EntrenadorEntity;
import com.gym.backend.Usuarios.Infrastructure.Entity.PersonaEntity;
import com.gym.backend.Usuarios.Infrastructure.Entity.UsuarioEntity;
import com.gym.backend.Usuarios.Infrastructure.Jpa.EmpleadoJpaRepository;
import com.gym.backend.Usuarios.Infrastructure.Jpa.EntrenadorJpaRepository;
import com.gym.backend.Usuarios.Infrastructure.Jpa.PersonaJpaRepository;
import com.gym.backend.Usuarios.Infrastructure.Jpa.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EntrenadorUseCase {

    private final EntrenadorJpaRepository entrenadorRepository;
    private final UsuarioJpaRepository usuarioRepository;
    private final PersonaJpaRepository personaRepository;
    private final EmpleadoJpaRepository empleadoRepository;

    /**
     * Convertir entidad a DTO
     */
    private EntrenadorResponse toResponse(EntrenadorEntity entity) {
        String nombre = "";
        String apellido = "";
        String email = "";
        String telefono = "";
        String fotoUrl = "";

        // Obtener datos de persona
        if (entity.getEmpleado() != null && entity.getEmpleado().getPersona() != null) {
            PersonaEntity persona = entity.getEmpleado().getPersona();
            nombre = persona.getNombre();
            apellido = persona.getApellido();
            telefono = persona.getTelefono();
            fotoUrl = persona.getFotoPerfilUrl();
        }

        // Obtener email del usuario
        UsuarioEntity usuario = usuarioRepository.findById(entity.getUsuarioId()).orElse(null);
        if (usuario != null) {
            email = usuario.getEmail();
        }

        return EntrenadorResponse.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId())
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .telefono(telefono)
                .fotoUrl(fotoUrl)
                .especialidad(entity.getEspecialidad())
                .especialidadNombre(entity.getEspecialidad() != null ? entity.getEspecialidad().getNombre() : null)
                .certificaciones(entity.getCertificaciones())
                .experienciaAnios(entity.getExperienciaAnios())
                .maxClientes(entity.getMaxClientes())
                .biografia(entity.getBiografia())
                .ratingPromedio(entity.getRatingPromedio())
                .totalResenas(entity.getTotalResenas())
                .activo(entity.getActivo())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }

    public List<EntrenadorResponse> listarTodos() {
        return entrenadorRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<EntrenadorResponse> listarActivos() {
        return entrenadorRepository.findByActivo(true).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<EntrenadorResponse> listarDisponibles() {
        return entrenadorRepository.findDisponibles().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<EntrenadorResponse> listarPorEspecialidad(EspecialidadEntrenador especialidad) {
        return entrenadorRepository.findByEspecialidad(especialidad).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<EntrenadorResponse> listarMejorValorados() {
        return entrenadorRepository.findByActivoTrueOrderByRatingPromedioDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EntrenadorResponse obtenerPorId(Long id) {
        EntrenadorEntity entity = entrenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));
        return toResponse(entity);
    }

    public EntrenadorResponse obtenerPorUsuarioId(Long usuarioId) {
        EntrenadorEntity entity = entrenadorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado para este usuario"));
        return toResponse(entity);
    }

    /**
     * Crear un entrenador a partir de un usuario existente con rol ENTRENADOR.
     */
    public EntrenadorResponse crearDesdeUsuario(Long usuarioId, EspecialidadEntrenador especialidad,
            String certificaciones, Integer experienciaAnios,
            Integer maxClientes, String biografia) {
        // Validar usuario
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getRoles().contains(Rol.ENTRENADOR)) {
            throw new RuntimeException("El usuario debe tener rol ENTRENADOR");
        }

        // Verificar si ya tiene entrenador
        if (entrenadorRepository.findByUsuarioId(usuarioId).isPresent()) {
            throw new RuntimeException("Este usuario ya es entrenador");
        }

        // Buscar o crear empleado
        EmpleadoEntity empleado = empleadoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> crearEmpleadoParaUsuario(usuario));

        // Crear entrenador
        EntrenadorEntity entrenador = EntrenadorEntity.builder()
                .empleado(empleado)
                .usuarioId(usuarioId)
                .especialidad(especialidad)
                .certificaciones(certificaciones)
                .experienciaAnios(experienciaAnios != null ? experienciaAnios : 0)
                .maxClientes(maxClientes != null ? maxClientes : 10)
                .biografia(biografia)
                .activo(true)
                .build();

        EntrenadorEntity saved = entrenadorRepository.save(entrenador);
        return toResponse(saved);
    }

    private EmpleadoEntity crearEmpleadoParaUsuario(UsuarioEntity usuario) {
        PersonaEntity persona = personaRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("El usuario no tiene datos personales registrados"));

        EmpleadoEntity empleado = EmpleadoEntity.builder()
                .persona(persona)
                .usuarioId(usuario.getId())
                .codigoEmpleado("ENT-" + usuario.getId())
                .fechaContratacion(LocalDate.now())
                .activo(true)
                .build();

        return empleadoRepository.save(empleado);
    }

    public EntrenadorResponse actualizar(Long id, EspecialidadEntrenador especialidad, String certificaciones,
            Integer experienciaAnios, Integer maxClientes, String biografia) {
        EntrenadorEntity entrenador = entrenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

        if (especialidad != null) {
            entrenador.setEspecialidad(especialidad);
        }
        if (certificaciones != null) {
            entrenador.setCertificaciones(certificaciones);
        }
        if (experienciaAnios != null) {
            entrenador.setExperienciaAnios(experienciaAnios);
        }
        if (maxClientes != null) {
            entrenador.setMaxClientes(maxClientes);
        }
        if (biografia != null) {
            entrenador.setBiografia(biografia);
        }

        EntrenadorEntity saved = entrenadorRepository.save(entrenador);
        return toResponse(saved);
    }

    public void activar(Long id) {
        EntrenadorEntity entrenador = entrenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));
        entrenador.setActivo(true);
        entrenadorRepository.save(entrenador);
    }

    public void desactivar(Long id) {
        EntrenadorEntity entrenador = entrenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));
        entrenador.setActivo(false);
        entrenadorRepository.save(entrenador);
    }

    public long contarActivos() {
        return entrenadorRepository.countByActivoTrue();
    }

    /**
     * Listar usuarios con rol ENTRENADOR que aún no tienen entrenador asignado
     */
    public List<UsuarioDisponibleDTO> listarUsuariosDisponibles() {
        List<UsuarioEntity> usuariosEntrenadores = usuarioRepository.findByRol(Rol.ENTRENADOR);
        List<Long> idsConEntrenador = entrenadorRepository.findAll().stream()
                .map(EntrenadorEntity::getUsuarioId)
                .toList();

        return usuariosEntrenadores.stream()
                .filter(u -> !idsConEntrenador.contains(u.getId()))
                .map(u -> {
                    PersonaEntity persona = personaRepository.findByUsuarioId(u.getId()).orElse(null);
                    return new UsuarioDisponibleDTO(
                            u.getId(),
                            u.getEmail(),
                            persona != null ? persona.getNombre() + " " + persona.getApellido() : u.getEmail());
                })
                .toList();
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class UsuarioDisponibleDTO {
        private Long id;
        private String email;
        private String nombreCompleto;
    }
}
