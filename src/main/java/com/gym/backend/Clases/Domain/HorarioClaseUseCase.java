package com.gym.backend.Clases.Domain;

import com.gym.backend.Clases.Infrastructure.Entity.HorarioClaseEntity;
import com.gym.backend.Clases.Infrastructure.Repository.HorarioClaseRepository;
import com.gym.backend.Clases.Infrastructure.Repository.InstructorRepository;
import com.gym.backend.Clases.Infrastructure.Repository.TipoClaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HorarioClaseUseCase {

    private final HorarioClaseRepository repository;
    private final TipoClaseRepository tipoClaseRepository;
    private final InstructorRepository instructorRepository;

    public List<HorarioClaseEntity> listarTodos() {
        return repository.findAll();
    }

    public List<HorarioClaseEntity> listarActivos() {
        return repository.findByActivoTrue();
    }

    public List<HorarioClaseEntity> listarPorDia(Integer diaSemana) {
        return repository.findByDiaSemanaAndActivoTrue(diaSemana);
    }

    public HorarioClaseEntity obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario de clase no encontrado"));
    }

    public HorarioClaseEntity crear(HorarioClaseEntity horario) {
        // Validar que existen el tipo de clase y el instructor
        if (!tipoClaseRepository.existsById(horario.getTipoClase().getId())) {
            throw new RuntimeException("Tipo de clase no encontrado");
        }
        if (!instructorRepository.existsById(horario.getInstructor().getId())) {
            throw new RuntimeException("Instructor no encontrado");
        }

        return repository.save(horario);
    }

    public HorarioClaseEntity actualizar(Long id, HorarioClaseEntity datosActualizados) {
        HorarioClaseEntity horario = obtenerPorId(id);

        horario.setTipoClase(datosActualizados.getTipoClase());
        horario.setInstructor(datosActualizados.getInstructor());
        horario.setDiaSemana(datosActualizados.getDiaSemana());
        horario.setHoraInicio(datosActualizados.getHoraInicio());
        horario.setAforoMaximo(datosActualizados.getAforoMaximo());
        horario.setSala(datosActualizados.getSala());

        return repository.save(horario);
    }

    public void desactivar(Long id) {
        HorarioClaseEntity horario = obtenerPorId(id);
        horario.setActivo(false);
        repository.save(horario);
    }

    public void activar(Long id) {
        HorarioClaseEntity horario = obtenerPorId(id);
        horario.setActivo(true);
        repository.save(horario);
    }
}
