package com.gym.backend.Progreso.Domain;

import com.gym.backend.Progreso.Domain.Enum.TipoFoto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para fotos de progreso
 */
public interface FotoProgresoRepositoryPort {
    FotoProgreso guardar(FotoProgreso foto);

    Optional<FotoProgreso> buscarPorId(Long id);

    List<FotoProgreso> buscarPorUsuario(Long usuarioId);

    List<FotoProgreso> buscarPorUsuarioYTipo(Long usuarioId, TipoFoto tipoFoto);

    List<FotoProgreso> buscarPorUsuarioYRango(Long usuarioId, LocalDate inicio, LocalDate fin);

    Optional<FotoProgreso> buscarUltimaPorUsuario(Long usuarioId);

    void eliminar(Long id);

    int contarPorUsuario(Long usuarioId);
}
