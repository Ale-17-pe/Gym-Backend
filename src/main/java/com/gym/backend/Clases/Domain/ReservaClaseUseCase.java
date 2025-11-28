package com.gym.backend.Clases.Domain;

import com.gym.backend.Clases.Domain.Enum.EstadoReserva;
import com.gym.backend.Clases.Domain.Enum.EstadoSesion;
import com.gym.backend.Clases.Domain.Enum.TipoPenalizacion;
import com.gym.backend.Clases.Infrastructure.Entity.*;
import com.gym.backend.Clases.Infrastructure.Repository.*;
import com.gym.backend.Membresias.Domain.MembresiaUseCase;
import com.gym.backend.Usuarios.Infrastructure.Jpa.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservaClaseUseCase {

    private final ReservaClaseRepository reservaRepository;
    private final SesionClaseRepository sesionRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;
    private final MembresiaUseCase membresiaUseCase;
    private final PenalizacionClaseRepository penalizacionRepository;

    private static final int MAX_RESERVAS_SIMULTANEAS = 5;
    private static final int HORAS_MINIMAS_CANCELACION = 2;
    private static final int PUNTOS_PENALIZACION_MAXIMOS = 3;
    private static final int DIAS_SUSPENSION = 7;

    /**
     * Reservar una clase
     */
    public ReservaClaseEntity reservar(Long sesionId, Long usuarioId) {
        // 1. Validar que el usuario existe y obtener entidad
        var usuario = usuarioJpaRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Validar que la sesión existe y está programada
        SesionClaseEntity sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("Sesión de clase no encontrada"));

        if (sesion.getEstado() != EstadoSesion.PROGRAMADA) {
            throw new RuntimeException("La sesión no está disponible para reservas");
        }

        // 3. Validar que el usuario tiene membresía activa
        boolean tieneMembresia = membresiaUseCase.verificarAcceso(usuarioId);
        if (!tieneMembresia) {
            throw new RuntimeException("Necesitas una membresía activa para reservar clases");
        }

        // 4. Validar que el usuario no está suspendido por penalizaciones
        validarPenalizaciones(usuarioId);

        // 5. Validar que el usuario no tiene ya una reserva para esta sesión
        if (reservaRepository.existsBySesionClaseIdAndUsuarioId(sesionId, usuarioId)) {
            throw new RuntimeException("Ya tienes una reserva para esta clase");
        }

        // 6. Validar límite de reservas simultáneas
        List<ReservaClaseEntity> reservasActivas = reservaRepository.findReservasActivasByUsuario(usuarioId);
        if (reservasActivas.size() >= MAX_RESERVAS_SIMULTANEAS) {
            throw new RuntimeException(
                    "Has alcanzado el límite de " + MAX_RESERVAS_SIMULTANEAS + " reservas simultáneas");
        }

        // 7. Verificar si hay cupo disponible
        Long reservasConfirmadas = reservaRepository.countReservasConfirmadasBySesion(sesionId);
        Integer aforoMaximo = sesion.getHorarioClase().getAforoMaximo();

        ReservaClaseEntity reserva = ReservaClaseEntity.builder()
                .sesionClase(sesion)
                .usuario(usuario)
                .build();

        if (reservasConfirmadas < aforoMaximo) {
            // Hay cupo - confirmar reserva
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            reserva.setPosicionListaEspera(null);

            // Incrementar contador de asistentes
            sesion.setAsistentesActuales(sesion.getAsistentesActuales() + 1);
            sesionRepository.save(sesion);
        } else {
            // Clase llena - agregar a lista de espera
            List<ReservaClaseEntity> listaEspera = reservaRepository.findListaEsperaBySesion(sesionId);
            int siguientePosicion = listaEspera.size() + 1;

            reserva.setEstado(EstadoReserva.LISTA_ESPERA);
            reserva.setPosicionListaEspera(siguientePosicion);
        }

        reserva = reservaRepository.save(reserva);

        // TODO: Enviar notificación de confirmación o lista de espera

        return reserva;
    }

    /**
     * Cancelar una reserva
     */
    public void cancelar(Long reservaId, Long usuarioId) {
        ReservaClaseEntity reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Validar que la reserva pertenece al usuario
        if (!reserva.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para cancelar esta reserva");
        }

        // Validar que la reserva está activa
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new RuntimeException("La reserva ya está cancelada");
        }

        // Validar tiempo de cancelación
        LocalDateTime fechaClase = reserva.getSesionClase().getFecha().atTime(
                reserva.getSesionClase().getHorarioClase().getHoraInicio());
        LocalDateTime ahora = LocalDateTime.now();
        long horasRestantes = java.time.Duration.between(ahora, fechaClase).toHours();

        if (horasRestantes < HORAS_MINIMAS_CANCELACION) {
            // Cancelación tardía - aplicar penalización
            aplicarPenalizacion(reserva, TipoPenalizacion.CANCELACION_TARDIA);
        }

        // Si era reserva confirmada, liberar cupo
        if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
            SesionClaseEntity sesion = reserva.getSesionClase();
            sesion.setAsistentesActuales(sesion.getAsistentesActuales() - 1);
            sesionRepository.save(sesion);

            // Buscar en lista de espera y promover al primero
            promoverDesdeListaEspera(sesion.getId());
        }

        // Marcar como cancelada
        reserva.setEstado(EstadoReserva.CANCELADA);
        reserva.setFechaCancelacion(LocalDateTime.now());
        reservaRepository.save(reserva);
    }

    /**
     * Marcar asistencia a una clase (para recepcionista)
     */
    public void marcarAsistencia(Long reservaId, boolean asistio) {
        ReservaClaseEntity reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new RuntimeException("Solo se puede marcar asistencia en reservas confirmadas");
        }

        if (asistio) {
            reserva.setEstado(EstadoReserva.ASISTIO);
            reserva.setAsistio(true);
        } else {
            // No asistió - aplicar penalización
            reserva.setEstado(EstadoReserva.NO_ASISTIO);
            reserva.setAsistio(false);
            aplicarPenalizacion(reserva, TipoPenalizacion.NO_ASISTENCIA);
        }

        reservaRepository.save(reserva);
    }

    /**
     * Obtener reservas de un usuario
     */
    public List<ReservaClaseEntity> obtenerReservasUsuario(Long usuarioId) {
        return reservaRepository.findReservasActivasByUsuario(usuarioId);
    }

    /**
     * Obtener reservas de una sesión
     */
    public List<ReservaClaseEntity> obtenerReservasSesion(Long sesionId) {
        return reservaRepository.findBySesionClaseId(sesionId);
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private void validarPenalizaciones(Long usuarioId) {
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        Integer puntosPenalizacion = penalizacionRepository.sumPuntosByUsuarioAndFechaAfter(usuarioId, hace30Dias);

        if (puntosPenalizacion != null && puntosPenalizacion >= PUNTOS_PENALIZACION_MAXIMOS) {
            throw new RuntimeException(
                    "Tu cuenta está suspendida por " + DIAS_SUSPENSION + " días debido a múltiples inasistencias");
        }
    }

    private void aplicarPenalizacion(ReservaClaseEntity reserva, TipoPenalizacion tipo) {
        PenalizacionClaseEntity penalizacion = PenalizacionClaseEntity.builder()
                .usuario(reserva.getUsuario())
                .reservaClase(reserva)
                .tipo(tipo)
                .puntos(1)
                .activo(true)
                .build();

        penalizacionRepository.save(penalizacion);
    }

    private void promoverDesdeListaEspera(Long sesionId) {
        List<ReservaClaseEntity> listaEspera = reservaRepository.findListaEsperaBySesion(sesionId);

        if (!listaEspera.isEmpty()) {
            // Promover al primero de la lista
            ReservaClaseEntity primera = listaEspera.get(0);
            primera.setEstado(EstadoReserva.CONFIRMADA);
            primera.setPosicionListaEspera(null);
            reservaRepository.save(primera);

            // Actualizar contador de sesión
            SesionClaseEntity sesion = sesionRepository.findById(sesionId).orElseThrow();
            sesion.setAsistentesActuales(sesion.getAsistentesActuales() + 1);
            sesionRepository.save(sesion);

            // Reordenar el resto de la lista
            for (int i = 1; i < listaEspera.size(); i++) {
                ReservaClaseEntity r = listaEspera.get(i);
                r.setPosicionListaEspera(i);
                reservaRepository.save(r);
            }

            // TODO: Enviar notificación al usuario promovido
        }
    }
}
