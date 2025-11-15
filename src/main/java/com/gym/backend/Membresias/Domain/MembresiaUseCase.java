package com.gym.backend.Membresias.Domain;

import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import com.gym.backend.Membresias.Domain.Exceptions.MembresiaNotFoundException;
import com.gym.backend.Membresias.Domain.Exceptions.MembresiaValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembresiaUseCase {
    private final MembresiaRepositoryPort repo;

    public Membresia crear(Membresia membresia) {
        membresia.validar();

        // Verificar si el usuario ya tiene membresía activa
        if (repo.buscarActivaPorUsuario(membresia.getUsuarioId()).isPresent()) {
            throw new MembresiaValidationException("El usuario ya tiene una membresía activa");
        }

        Membresia nuevaMembresia = Membresia.builder()
                .id(membresia.getId())
                .usuarioId(membresia.getUsuarioId())
                .planId(membresia.getPlanId())
                .pagoId(membresia.getPagoId())
                .fechaInicio(membresia.getFechaInicio())
                .fechaFin(membresia.getFechaFin())
                .estado(EstadoMembresia.ACTIVA)
                .fechaCreacion(LocalDate.now())
                .fechaActualizacion(LocalDate.now())
                .build();

        return repo.guardar(nuevaMembresia);
    }

    public Membresia extender(Long membresiaId, Integer diasExtension) {
        Membresia membresia = obtener(membresiaId);
        LocalDate nuevaFechaFin = membresia.getFechaFin().plusDays(diasExtension);

        Membresia actualizada = Membresia.builder()
                .id(membresia.getId())
                .usuarioId(membresia.getUsuarioId())
                .planId(membresia.getPlanId())
                .pagoId(membresia.getPagoId())
                .fechaInicio(membresia.getFechaInicio())
                .fechaFin(nuevaFechaFin)
                .estado(membresia.getEstado())
                .fechaCreacion(membresia.getFechaCreacion())
                .fechaActualizacion(LocalDate.now())
                .build();

        return repo.actualizar(actualizada);
    }

    public Membresia obtener(Long id) {
        return repo.buscarPorId(id).orElseThrow(() -> new MembresiaNotFoundException(id));
    }

    public List<Membresia> listar() { return repo.listar(); }
    public List<Membresia> listarPorUsuario(Long usuarioId) { return repo.listarPorUsuario(usuarioId); }
    public Membresia obtenerActivaPorUsuario(Long usuarioId) {
        return repo.buscarActivaPorUsuario(usuarioId).orElse(null);
    }
}