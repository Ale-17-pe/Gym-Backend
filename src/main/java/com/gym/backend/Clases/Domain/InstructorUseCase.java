package com.gym.backend.Clases.Domain;

import com.gym.backend.Clases.Infrastructure.Entity.InstructorEntity;
import com.gym.backend.Clases.Infrastructure.Repository.InstructorRepository;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Infrastructure.Jpa.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class InstructorUseCase {

    private final InstructorRepository instructorRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;

    public List<InstructorEntity> listarTodos() {
        return instructorRepository.findAll();
    }

    public List<InstructorEntity> listarActivos() {
        return instructorRepository.findByActivoTrue();
    }

    public InstructorEntity obtenerPorId(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instructor no encontrado"));
    }

    public InstructorEntity obtenerPorUsuarioId(Long usuarioId) {
        return instructorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Instructor no encontrado para este usuario"));
    }

    public InstructorEntity crear(Long usuarioId, String especialidades, String biografia, String fotoPerfil) {
        // Validar que el usuario existe y tiene rol INSTRUCTOR
        var usuario = usuarioJpaRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRol() != Rol.INSTRUCTOR) {
            throw new RuntimeException("El usuario debe tener rol INSTRUCTOR");
        }

        // Verificar que no existe ya un instructor para este usuario
        if (instructorRepository.existsByUsuarioId(usuarioId)) {
            throw new RuntimeException("Ya existe un instructor para este usuario");
        }

        InstructorEntity instructor = InstructorEntity.builder()
                .usuario(usuario)
                .especialidades(especialidades)
                .biografia(biografia)
                .fotoPerfil(fotoPerfil)
                .activo(true)
                .build();

        return instructorRepository.save(instructor);
    }

    public InstructorEntity actualizar(Long id, String especialidades, String biografia, String fotoPerfil) {
        InstructorEntity instructor = obtenerPorId(id);

        instructor.setEspecialidades(especialidades);
        instructor.setBiografia(biografia);
        instructor.setFotoPerfil(fotoPerfil);

        return instructorRepository.save(instructor);
    }

    public void desactivar(Long id) {
        InstructorEntity instructor = obtenerPorId(id);
        instructor.setActivo(false);
        instructorRepository.save(instructor);
    }

    public void activar(Long id) {
        InstructorEntity instructor = obtenerPorId(id);
        instructor.setActivo(true);
        instructorRepository.save(instructor);
    }
}
