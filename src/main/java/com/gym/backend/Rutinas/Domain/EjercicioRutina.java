package com.gym.backend.Rutinas.Domain;

import lombok.*;

/**
 * Entidad de dominio para un ejercicio dentro de un día de rutina
 * Contiene la configuración específica: series, repeticiones, descanso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EjercicioRutina {
    private Long id;
    private Long diaRutinaId;
    private Long ejercicioId;
    private String nombreEjercicio; // Desnormalizado para acceso rápido
    private Integer series;
    private Integer repeticiones;
    private String rangoRepeticiones; // "8-12" si es rango flexible
    private Integer descansoSegundos;
    private Double pesoSugerido; // Peso sugerido en kg
    private Integer orden; // Orden dentro del día
    private String notas; // Notas personales del cliente
    private String tempo; // "3-1-2" (excéntrico-pausa-concéntrico)

    // Datos del ejercicio (para mostrar en UI)
    private Ejercicio ejercicio;
}
