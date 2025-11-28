package com.gym.backend.Clases.Infrastructure.Repository;

import com.gym.backend.Clases.Domain.Enum.EstadoReserva;
import com.gym.backend.Clases.Infrastructure.Entity.ReservaClaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaClaseRepository extends JpaRepository<ReservaClaseEntity, Long> {

    List<ReservaClaseEntity> findByUsuarioId(Long usuarioId);

    List<ReservaClaseEntity> findBySesionClaseId(Long sesionClaseId);

    Optional<ReservaClaseEntity> findBySesionClaseIdAndUsuarioId(Long sesionClaseId, Long usuarioId);

    List<ReservaClaseEntity> findByEstado(EstadoReserva estado);

    @Query("SELECT r FROM ReservaClaseEntity r JOIN FETCH r.sesionClase s JOIN FETCH s.horarioClase h JOIN FETCH h.tipoClase JOIN FETCH h.instructor i JOIN FETCH i.usuario WHERE r.usuario.id = :usuarioId AND r.estado IN ('CONFIRMADA', 'LISTA_ESPERA') ORDER BY s.fecha")
    List<ReservaClaseEntity> findReservasActivasByUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT r FROM ReservaClaseEntity r WHERE r.sesionClase.id = :sesionId AND r.estado = 'CONFIRMADA'")
    List<ReservaClaseEntity> findReservasConfirmadasBySesion(@Param("sesionId") Long sesionId);

    @Query("SELECT r FROM ReservaClaseEntity r WHERE r.sesionClase.id = :sesionId AND r.estado = 'LISTA_ESPERA' ORDER BY r.posicionListaEspera")
    List<ReservaClaseEntity> findListaEsperaBySesion(@Param("sesionId") Long sesionId);

    @Query("SELECT COUNT(r) FROM ReservaClaseEntity r WHERE r.sesionClase.id = :sesionId AND r.estado = 'CONFIRMADA'")
    Long countReservasConfirmadasBySesion(@Param("sesionId") Long sesionId);

    boolean existsBySesionClaseIdAndUsuarioId(Long sesionClaseId, Long usuarioId);
}
