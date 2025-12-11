package com.gym.backend.Progreso.Infrastructure.Adapter;

import com.gym.backend.Progreso.Domain.MedidaCorporal;
import com.gym.backend.Progreso.Domain.MedidaCorporalRepositoryPort;
import com.gym.backend.Progreso.Infrastructure.Entity.MedidaCorporalEntity;
import com.gym.backend.Progreso.Infrastructure.Jpa.MedidaCorporalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MedidaCorporalRepositoryAdapter implements MedidaCorporalRepositoryPort {

    private final MedidaCorporalJpaRepository jpa;

    @Override
    public MedidaCorporal guardar(MedidaCorporal medida) {
        return toDomain(jpa.save(toEntity(medida)));
    }

    @Override
    public Optional<MedidaCorporal> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<MedidaCorporal> buscarPorUsuario(Long usuarioId) {
        return jpa.findByUsuarioIdOrderByFechaDesc(usuarioId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<MedidaCorporal> buscarPorUsuarioYRango(Long usuarioId, LocalDate inicio, LocalDate fin) {
        return jpa.findByUsuarioIdAndFechaBetweenOrderByFechaAsc(usuarioId, inicio, fin).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public Optional<MedidaCorporal> buscarUltimaPorUsuario(Long usuarioId) {
        return jpa.findFirstByUsuarioIdOrderByFechaDesc(usuarioId).map(this::toDomain);
    }

    @Override
    public Optional<MedidaCorporal> buscarPorUsuarioYFecha(Long usuarioId, LocalDate fecha) {
        return jpa.findByUsuarioIdAndFecha(usuarioId, fecha).map(this::toDomain);
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    private MedidaCorporal toDomain(MedidaCorporalEntity e) {
        return MedidaCorporal.builder()
                .id(e.getId())
                .usuarioId(e.getUsuarioId())
                .fecha(e.getFecha())
                .pesoKg(e.getPesoKg())
                .alturaM(e.getAlturaM())
                .pecho(e.getPecho())
                .cintura(e.getCintura())
                .cadera(e.getCadera())
                .cuello(e.getCuello())
                .brazoIzquierdo(e.getBrazoIzquierdo())
                .brazoDerecho(e.getBrazoDerecho())
                .antebrazoIzquierdo(e.getAntebrazoIzquierdo())
                .antebrazoDerecho(e.getAntebrazoDerecho())
                .musloIzquierdo(e.getMusloIzquierdo())
                .musloDerecho(e.getMusloDerecho())
                .pantorrillaIzquierda(e.getPantorrillaIzquierda())
                .pantorrillaDerecha(e.getPantorrillaDerecha())
                .hombros(e.getHombros())
                .porcentajeGrasa(e.getPorcentajeGrasa())
                .porcentajeMusculo(e.getPorcentajeMusculo())
                .porcentajeAgua(e.getPorcentajeAgua())
                .masaOsea(e.getMasaOsea())
                .notas(e.getNotas())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    private MedidaCorporalEntity toEntity(MedidaCorporal m) {
        return MedidaCorporalEntity.builder()
                .id(m.getId())
                .usuarioId(m.getUsuarioId())
                .fecha(m.getFecha())
                .pesoKg(m.getPesoKg())
                .alturaM(m.getAlturaM())
                .pecho(m.getPecho())
                .cintura(m.getCintura())
                .cadera(m.getCadera())
                .cuello(m.getCuello())
                .brazoIzquierdo(m.getBrazoIzquierdo())
                .brazoDerecho(m.getBrazoDerecho())
                .antebrazoIzquierdo(m.getAntebrazoIzquierdo())
                .antebrazoDerecho(m.getAntebrazoDerecho())
                .musloIzquierdo(m.getMusloIzquierdo())
                .musloDerecho(m.getMusloDerecho())
                .pantorrillaIzquierda(m.getPantorrillaIzquierda())
                .pantorrillaDerecha(m.getPantorrillaDerecha())
                .hombros(m.getHombros())
                .porcentajeGrasa(m.getPorcentajeGrasa())
                .porcentajeMusculo(m.getPorcentajeMusculo())
                .porcentajeAgua(m.getPorcentajeAgua())
                .masaOsea(m.getMasaOsea())
                .notas(m.getNotas())
                .fechaCreacion(m.getFechaCreacion())
                .build();
    }
}
