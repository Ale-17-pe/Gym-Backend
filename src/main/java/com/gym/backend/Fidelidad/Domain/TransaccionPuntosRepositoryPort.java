package com.gym.backend.Fidelidad.Domain;

import com.gym.backend.Fidelidad.Domain.Enum.MotivoGanancia;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Puerto de repositorio para TransaccionPuntos
 */
public interface TransaccionPuntosRepositoryPort {

    TransaccionPuntos guardar(TransaccionPuntos transaccion);

    List<TransaccionPuntos> listarPorUsuario(Long usuarioId);

    List<TransaccionPuntos> listarPorUsuarioPaginado(Long usuarioId, int pagina, int tamano);

    /**
     * Verifica si ya existe una transacción con la misma referencia
     * (para evitar duplicados, ej: puntos por misma asistencia)
     */
    boolean existePorReferenciaYMotivo(Long referenciaId, String tipoReferencia, MotivoGanancia motivo);

    /**
     * Cuenta transacciones de un tipo específico para un usuario en un período
     */
    long contarPorUsuarioYMotivoDesde(Long usuarioId, MotivoGanancia motivo, LocalDateTime desde);

    /**
     * Suma total de puntos ganados por un usuario
     */
    int sumarPuntosGanadosPorUsuario(Long usuarioId);
}
