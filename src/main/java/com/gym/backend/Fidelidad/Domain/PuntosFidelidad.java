package com.gym.backend.Fidelidad.Domain;

import com.gym.backend.Fidelidad.Domain.Enum.NivelFidelidad;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa el balance de puntos de un usuario
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuntosFidelidad {
    private Long id;
    private Long usuarioId;
    private Integer puntosTotales; // Puntos acumulados históricos
    private Integer puntosDisponibles; // Puntos disponibles para canjear
    private Integer puntosCanjeados; // Total de puntos canjeados
    private NivelFidelidad nivel;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    /**
     * Agrega puntos al balance
     */
    public void agregarPuntos(int puntos) {
        if (puntos <= 0) {
            throw new IllegalArgumentException("Los puntos deben ser positivos");
        }

        // Aplicar multiplicador de nivel
        int puntosConBonus = (int) Math.round(puntos * nivel.getMultiplicador());

        this.puntosTotales += puntosConBonus;
        this.puntosDisponibles += puntosConBonus;
        this.fechaActualizacion = LocalDateTime.now();

        // Recalcular nivel
        this.nivel = NivelFidelidad.calcularNivel(this.puntosTotales);
    }

    /**
     * Canjea puntos del balance
     */
    public void canjearPuntos(int puntos) {
        if (puntos <= 0) {
            throw new IllegalArgumentException("Los puntos deben ser positivos");
        }
        if (puntos > this.puntosDisponibles) {
            throw new IllegalStateException("Puntos insuficientes. Disponibles: " + puntosDisponibles);
        }

        this.puntosDisponibles -= puntos;
        this.puntosCanjeados += puntos;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Devuelve puntos (por cancelación de canje)
     */
    public void devolverPuntos(int puntos) {
        if (puntos <= 0) {
            throw new IllegalArgumentException("Los puntos deben ser positivos");
        }

        this.puntosDisponibles += puntos;
        this.puntosCanjeados -= puntos;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Verifica si tiene suficientes puntos
     */
    public boolean tienePuntosSuficientes(int puntos) {
        return this.puntosDisponibles >= puntos;
    }

    /**
     * Calcula el progreso hacia el siguiente nivel
     */
    public int progresoPorcentaje() {
        if (nivel == NivelFidelidad.PLATINO) {
            return 100;
        }

        int puntosEnNivel = puntosTotales - nivel.getPuntosMinimos();
        int rangoNivel = nivel.getPuntosMaximos() - nivel.getPuntosMinimos() + 1;

        return Math.min(100, (int) ((puntosEnNivel * 100.0) / rangoNivel));
    }

    /**
     * Puntos necesarios para el siguiente nivel
     */
    public int puntosParaSiguienteNivel() {
        if (nivel == NivelFidelidad.PLATINO) {
            return 0;
        }
        return nivel.getPuntosMaximos() - puntosTotales + 1;
    }

    /**
     * Crea un balance inicial para un nuevo usuario
     */
    public static PuntosFidelidad crearNuevo(Long usuarioId) {
        return PuntosFidelidad.builder()
                .usuarioId(usuarioId)
                .puntosTotales(0)
                .puntosDisponibles(0)
                .puntosCanjeados(0)
                .nivel(NivelFidelidad.BRONCE)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }
}
