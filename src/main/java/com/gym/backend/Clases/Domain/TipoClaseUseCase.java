package com.gym.backend.Clases.Domain;

import com.gym.backend.Clases.Infrastructure.Entity.TipoClaseEntity;
import com.gym.backend.Clases.Infrastructure.Repository.TipoClaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TipoClaseUseCase {

    private final TipoClaseRepository repository;

    public List<TipoClaseEntity> listarTodos() {
        return repository.findAll();
    }

    public List<TipoClaseEntity> listarActivos() {
        return repository.findByActivoTrue();
    }

    public TipoClaseEntity obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de clase no encontrado"));
    }

    public TipoClaseEntity crear(TipoClaseEntity tipoClase) {
        return repository.save(tipoClase);
    }

    public TipoClaseEntity actualizar(Long id, TipoClaseEntity datosActualizados) {
        TipoClaseEntity tipoClase = obtenerPorId(id);

        tipoClase.setNombre(datosActualizados.getNombre());
        tipoClase.setDescripcion(datosActualizados.getDescripcion());
        tipoClase.setDuracionMinutos(datosActualizados.getDuracionMinutos());
        tipoClase.setNivel(datosActualizados.getNivel());
        tipoClase.setIcono(datosActualizados.getIcono());
        tipoClase.setColor(datosActualizados.getColor());

        return repository.save(tipoClase);
    }

    public void desactivar(Long id) {
        TipoClaseEntity tipoClase = obtenerPorId(id);
        tipoClase.setActivo(false);
        repository.save(tipoClase);
    }

    public void activar(Long id) {
        TipoClaseEntity tipoClase = obtenerPorId(id);
        tipoClase.setActivo(true);
        repository.save(tipoClase);
    }
}
