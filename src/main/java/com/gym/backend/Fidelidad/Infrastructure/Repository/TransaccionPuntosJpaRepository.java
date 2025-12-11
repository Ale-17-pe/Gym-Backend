package com.gym.backend.Fidelidad.Infrastructure.Repository;

import com.gym.backend.Fidelidad.Domain.Enum.MotivoGanancia;
import com.gym.backend.Fidelidad.Domain.Enum.TipoTransaccion;
import com.gym.backend.Fidelidad.Infrastructure.Entity.TransaccionPuntosEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionPuntosJpaRepository extends JpaRepository<TransaccionPuntosEntity, Long> {

    List<TransaccionPuntosEntity> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    List<TransaccionPuntosEntity> findByUsuarioIdOrderByFechaDesc(Long usuarioId, Pageable pageable);

    boolean existsByReferenciaIdAndTipoReferenciaAndMotivo(Long referenciaId, String tipoReferencia,
            MotivoGanancia motivo);

    long countByUsuarioIdAndMotivoAndFechaAfter(Long usuarioId, MotivoGanancia motivo, LocalDateTime desde);

    @Query("SELECT COALESCE(SUM(t.puntos), 0) FROM TransaccionPuntosEntity t " +
            "WHERE t.usuarioId = :usuarioId AND t.tipo = :tipo")
    Integer sumPuntosByUsuarioIdAndTipo(@Param("usuarioId") Long usuarioId, @Param("tipo") TipoTransaccion tipo);
}
