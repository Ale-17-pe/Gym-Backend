package com.gym.backend.Progreso.Infrastructure.Adapter;

import com.gym.backend.Progreso.Domain.Enum.TipoFoto;
import com.gym.backend.Progreso.Domain.FotoProgreso;
import com.gym.backend.Progreso.Domain.FotoProgresoRepositoryPort;
import com.gym.backend.Progreso.Infrastructure.Entity.FotoProgresoEntity;
import com.gym.backend.Progreso.Infrastructure.Jpa.FotoProgresoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FotoProgresoRepositoryAdapter implements FotoProgresoRepositoryPort {

    private final FotoProgresoJpaRepository jpa;

    @Override
    public FotoProgreso guardar(FotoProgreso foto) {
        return toDomain(jpa.save(toEntity(foto)));
    }

    @Override
    public Optional<FotoProgreso> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<FotoProgreso> buscarPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioIdOrderByFechaDesc(usuarioId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<FotoProgreso> buscarPorUsuarioYTipo(Long usuarioId, TipoFoto tipoFoto) {
        return jpa.findByUsuarioIdAndTipoFotoOrderByFechaDesc(usuarioId, tipoFoto).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<FotoProgreso> buscarPorUsuarioYRango(Long usuarioId, LocalDate inicio, LocalDate fin) {
        return jpa.findByUsuarioIdAndFechaBetweenOrderByFechaAsc(usuarioId, inicio, fin).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public Optional<FotoProgreso> buscarUltimaPorUsuario(Long usuarioId) {
        return jpa.findFirstByUsuarioIdOrderByFechaDesc(usuarioId).map(this::toDomain);
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public int contarPorUsuario(Long usuarioId) {
        return jpa.countByUsuarioId(usuarioId);
    }

    private FotoProgreso toDomain(FotoProgresoEntity e) {
        return FotoProgreso.builder()
                .id(e.getId())
                .usuarioId(e.getUsuarioId())
                .fecha(e.getFecha())
                .tipoFoto(e.getTipoFoto())
                .urlImagen(e.getUrlImagen())
                .nombreArchivo(e.getNombreArchivo())
                .tamanoBytes(e.getTamanoBytes())
                .notas(e.getNotas())
                .pesoEnFoto(e.getPesoEnFoto())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    private FotoProgresoEntity toEntity(FotoProgreso f) {
        return FotoProgresoEntity.builder()
                .id(f.getId())
                .usuarioId(f.getUsuarioId())
                .fecha(f.getFecha())
                .tipoFoto(f.getTipoFoto())
                .urlImagen(f.getUrlImagen())
                .nombreArchivo(f.getNombreArchivo())
                .tamanoBytes(f.getTamanoBytes())
                .notas(f.getNotas())
                .pesoEnFoto(f.getPesoEnFoto())
                .fechaCreacion(f.getFechaCreacion())
                .build();
    }
}
