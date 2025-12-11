package com.gym.backend.Fidelidad.Domain.Enum;

/**
 * Niveles de fidelidad del cliente
 */
public enum NivelFidelidad {
    BRONCE(0, 999, 1.0),
    PLATA(1000, 4999, 1.1),
    ORO(5000, 14999, 1.25),
    PLATINO(15000, Integer.MAX_VALUE, 1.5);

    private final int puntosMinimos;
    private final int puntosMaximos;
    private final double multiplicador;

    NivelFidelidad(int puntosMinimos, int puntosMaximos, double multiplicador) {
        this.puntosMinimos = puntosMinimos;
        this.puntosMaximos = puntosMaximos;
        this.multiplicador = multiplicador;
    }

    public int getPuntosMinimos() {
        return puntosMinimos;
    }

    public int getPuntosMaximos() {
        return puntosMaximos;
    }

    public double getMultiplicador() {
        return multiplicador;
    }

    /**
     * Calcula el nivel basado en puntos totales acumulados
     */
    public static NivelFidelidad calcularNivel(int puntosTotales) {
        for (NivelFidelidad nivel : values()) {
            if (puntosTotales >= nivel.puntosMinimos && puntosTotales <= nivel.puntosMaximos) {
                return nivel;
            }
        }
        return BRONCE;
    }
}
