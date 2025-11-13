package com.gym.backend.Membresias.Domain;

import java.time.LocalDate;
import java.util.List;

public class MembresiaUseCase {

    private final MembresiaRepositoryPort repo;

    public MembresiaUseCase(MembresiaRepositoryPort repo) {
        this.repo = repo;
    }

    public Membresia crear(Membresia membresia, int duracionDias) {

        // Validar si el usuario ya tiene una activa
        if (repo.buscarActivaPorUsuario(membresia.getUsuarioId()) != null) {
            throw new IllegalStateException("El usuario ya tiene una membresía activa.");
        }

        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.plusDays(duracionDias);

        Membresia nueva = Membresia.builder()
                .id(null)
                .usuarioId(membresia.getUsuarioId())
                .planId(membresia.getPlanId())
                .fechaInicio(inicio)
                .fechaFin(fin)
                .estado("ACTIVA")
                .build();

        return repo.crear(nueva);
    }

    public Membresia actualizar(Long id, Membresia membresia) {
        return repo.actualizar(id, membresia);
    }

    public Membresia obtener(Long id) {
        return repo.obtenerPorId(id);
    }

    public List<Membresia> listar() {
        return repo.listar();
    }

    public List<Membresia> porUsuario(Long usuarioId) {
        return repo.buscarPorUsuario(usuarioId);
    }

    public Membresia activaPorUsuario(Long usuarioId) {
        return repo.buscarActivaPorUsuario(usuarioId);
    }

    public void eliminar(Long id) {
        repo.eliminar(id);
    }
}
