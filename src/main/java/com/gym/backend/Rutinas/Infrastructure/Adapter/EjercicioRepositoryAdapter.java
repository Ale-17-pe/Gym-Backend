package com.gym.backend.Rutinas.Infrastructure.Adapter;

import com.gym.backend.Rutinas.Domain.Ejercicio;
import com.gym.backend.Rutinas.Domain.EjercicioRepositoryPort;
import com.gym.backend.Rutinas.Domain.Enum.GrupoMuscular;
import com.gym.backend.Rutinas.Infrastructure.Entity.EjercicioEntity;
import com.gym.backend.Rutinas.Infrastructure.Jpa.EjercicioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EjercicioRepositoryAdapter implements EjercicioRepositoryPort {

    private final EjercicioJpaRepository jpa;

    @Override
    public Ejercicio guardar(Ejercicio ejercicio) {
        return toDomain(jpa.save(toEntity(ejercicio)));
    }

    @Override
    public Optional<Ejercicio> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Ejercicio> buscarPorGrupoMuscular(GrupoMuscular grupoMuscular) {
        return jpa.findByGrupoMuscular(grupoMuscular).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<Ejercicio> buscarPorNombre(String nombre) {
        return jpa.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<Ejercicio> listarActivos() {
        return jpa.findByActivoTrueOrderByGrupoMuscularAscNombreAsc().stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<Ejercicio> listar() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    private Ejercicio toDomain(EjercicioEntity e) {
        return Ejercicio.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .grupoMuscular(e.getGrupoMuscular())
                .grupoMuscularSecundario(e.getGrupoMuscularSecundario())
                .dificultad(e.getDificultad())
                .equipamiento(e.getEquipamiento())
                .instrucciones(e.getInstrucciones())
                .imagenUrl(e.getImagenUrl())
                .videoUrl(e.getVideoUrl())
                .activo(e.isActivo())
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    private EjercicioEntity toEntity(Ejercicio e) {
        return EjercicioEntity.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .grupoMuscular(e.getGrupoMuscular())
                .grupoMuscularSecundario(e.getGrupoMuscularSecundario())
                .dificultad(e.getDificultad())
                .equipamiento(e.getEquipamiento())
                .instrucciones(e.getInstrucciones())
                .imagenUrl(e.getImagenUrl())
                .videoUrl(e.getVideoUrl())
                .activo(e.isActivo())
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }
}
