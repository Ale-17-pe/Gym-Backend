package com.gym.backend.Clases.Domain.Enum;

public enum DiaSemana {
    LUNES(1),
    MARTES(2),
    MIERCOLES(3),
    JUEVES(4),
    VIERNES(5),
    SABADO(6),
    DOMINGO(7);

    private final int valor;

    DiaSemana(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public static DiaSemana fromValor(int valor) {
        for (DiaSemana dia : DiaSemana.values()) {
            if (dia.valor == valor) {
                return dia;
            }
        }
        throw new IllegalArgumentException("Valor de día inválido: " + valor);
    }
}
