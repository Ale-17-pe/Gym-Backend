package com.gym.backend.Asistencias.Domain;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AsistenciaUseCase {

    private final AsistenciaRepositoryPort repo;
    private final MembresiaValidatorPort membresiaValidator;

    public AsistenciaUseCase(
            AsistenciaRepositoryPort repo,
            MembresiaValidatorPort membresiaValidator
    ) {
        this.repo = repo;
        this.membresiaValidator = membresiaValidator;
    }

    public Asistencia registrar(Long usuarioId) {

        // Validar membresía
        if (!membresiaValidator.tieneMembresiaActiva(usuarioId)) {
            throw new IllegalStateException("El usuario no tiene una membresía activa.");
        }

        // Validar duplicado
        LocalDate hoy = LocalDate.now();

        LocalDateTime desde = hoy.atStartOfDay();
        LocalDateTime hasta = hoy.atTime(23, 59, 59);

        boolean existe = repo.existeAsistenciaHoy(usuarioId, desde, hasta);

        if (existe)
            throw new IllegalStateException("El usuario ya registró asistencia hoy.");

        // Registrar
        Asistencia nueva = Asistencia.builder()
                .id(null)
                .usuarioId(usuarioId)
                .fechaHora(LocalDateTime.now())
                .build();

        return repo.registrar(nueva);
    }

    public List<Asistencia> listar() {
        return repo.listar();
    }

    public List<Asistencia> porUsuario(Long usuarioId) {
        return repo.listarPorUsuario(usuarioId);
    }
}
