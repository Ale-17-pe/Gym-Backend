package com.gym.backend.Notificacion.Infrastructure;

import com.gym.backend.Notificacion.Domain.Notificacion;
import com.gym.backend.Notificacion.Domain.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

        // Obtener todas las notificaciones de un usuario ordenadas por fecha
        List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

        // Contar notificaciones no leídas de un usuario
        Long countByUsuarioIdAndLeidaFalse(Long usuarioId);

        // Obtener solo notificaciones no leídas
        List<Notificacion> findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(Long usuarioId);

        // Obtener notificaciones por tipo
        List<Notificacion> findByUsuarioIdAndTipoOrderByFechaCreacionDesc(Long usuarioId, TipoNotificacion tipo);

        // Obtener notificaciones recientes (últimos 30 días)
        @Query("SELECT n FROM Notificacion n WHERE n.usuarioId = :usuarioId " +
                        "AND n.fechaCreacion >= :fechaInicio ORDER BY n.fechaCreacion DESC")
        List<Notificacion> findRecentesByUsuarioId(
                        @Param("usuarioId") Long usuarioId,
                        @Param("fechaInicio") LocalDateTime fechaInicio);

        // Eliminar notificaciones antiguas (más de 90 días)
        @org.springframework.data.jpa.repository.Modifying
        @Query("DELETE FROM Notificacion n WHERE n.fechaCreacion < :fechaLimite")
        void deleteAntiguasAntesDe(@Param("fechaLimite") LocalDateTime fechaLimite);
}
