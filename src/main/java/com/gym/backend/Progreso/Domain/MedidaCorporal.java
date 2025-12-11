package com.gym.backend.Progreso.Domain;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad de dominio para las medidas corporales del cliente
 * Permite hacer seguimiento del peso, medidas y composición corporal
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedidaCorporal {
    private Long id;
    private Long usuarioId;
    private LocalDate fecha;

    // Peso y altura
    private Double pesoKg;
    private Double alturaM;

    // Medidas en centímetros
    private Double pecho;
    private Double cintura;
    private Double cadera;
    private Double cuello;
    private Double brazoIzquierdo;
    private Double brazoDerecho;
    private Double antebrazoIzquierdo;
    private Double antebrazoDerecho;
    private Double musloIzquierdo;
    private Double musloDerecho;
    private Double pantorrillaIzquierda;
    private Double pantorrillaDerecha;
    private Double hombros;

    // Composición corporal (opcional, si tienen báscula inteligente)
    private Double porcentajeGrasa;
    private Double porcentajeMusculo;
    private Double porcentajeAgua;
    private Double masaOsea;

    // Notas adicionales
    private String notas;

    private LocalDateTime fechaCreacion;

    /**
     * Calcula el IMC (Índice de Masa Corporal)
     */
    public Double calcularIMC() {
        if (pesoKg == null || alturaM == null || alturaM == 0) {
            return null;
        }
        return pesoKg / (alturaM * alturaM);
    }

    /**
     * Interpretación del IMC
     */
    public String interpretarIMC() {
        Double imc = calcularIMC();
        if (imc == null)
            return "No disponible";

        if (imc < 18.5)
            return "Bajo peso";
        if (imc < 25)
            return "Peso normal";
        if (imc < 30)
            return "Sobrepeso";
        if (imc < 35)
            return "Obesidad grado I";
        if (imc < 40)
            return "Obesidad grado II";
        return "Obesidad grado III";
    }

    /**
     * Calcula la relación cintura-cadera (indicador de salud)
     */
    public Double calcularRelacionCinturaCadera() {
        if (cintura == null || cadera == null || cadera == 0) {
            return null;
        }
        return cintura / cadera;
    }
}
