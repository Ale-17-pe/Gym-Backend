package com.gym.backend.Planes.Domain;

import com.gym.backend.Planes.Domain.Exceptions.PlanValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad de dominio Plan
 */
@DisplayName("Plan Domain Tests")
class PlanTest {

    private Plan.PlanBuilder planBuilder;

    @BeforeEach
    void setUp() {
        planBuilder = Plan.builder()
                .id(1L)
                .nombrePlan("Plan Premium")
                .descripcion("Acceso completo al gimnasio")
                .precio(99.99)
                .duracionDias(30)
                .activo(true)
                .beneficios("Acceso 24/7, Clases grupales, Entrenador personal")
                .categoria("PREMIUM")
                .vecesContratado(0)
                .ratingPromedio(0.0)
                .destacado(false)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Validación de Plan")
    class ValidacionTests {

        @Test
        @DisplayName("Plan válido no lanza excepción")
        void planValido_NoLanzaExcepcion() {
            Plan plan = planBuilder.build();
            assertDoesNotThrow(plan::validar);
        }

        @Test
        @DisplayName("Nombre nulo lanza excepción")
        void nombreNulo_LanzaExcepcion() {
            Plan plan = planBuilder.nombrePlan(null).build();

            PlanValidationException exception = assertThrows(
                    PlanValidationException.class,
                    plan::validar);

            assertEquals("El nombre del plan es requerido", exception.getMessage());
        }

        @Test
        @DisplayName("Nombre vacío lanza excepción")
        void nombreVacio_LanzaExcepcion() {
            Plan plan = planBuilder.nombrePlan("   ").build();

            assertThrows(PlanValidationException.class, plan::validar);
        }

        @Test
        @DisplayName("Precio nulo lanza excepción")
        void precioNulo_LanzaExcepcion() {
            Plan plan = planBuilder.precio(null).build();

            PlanValidationException exception = assertThrows(
                    PlanValidationException.class,
                    plan::validar);

            assertEquals("El precio debe ser mayor a 0", exception.getMessage());
        }

        @Test
        @DisplayName("Precio cero lanza excepción")
        void precioCero_LanzaExcepcion() {
            Plan plan = planBuilder.precio(0.0).build();

            assertThrows(PlanValidationException.class, plan::validar);
        }

        @Test
        @DisplayName("Precio negativo lanza excepción")
        void precioNegativo_LanzaExcepcion() {
            Plan plan = planBuilder.precio(-50.0).build();

            assertThrows(PlanValidationException.class, plan::validar);
        }

        @Test
        @DisplayName("Duración nula lanza excepción")
        void duracionNula_LanzaExcepcion() {
            Plan plan = planBuilder.duracionDias(null).build();

            PlanValidationException exception = assertThrows(
                    PlanValidationException.class,
                    plan::validar);

            assertEquals("La duración en días debe ser mayor a 0", exception.getMessage());
        }

        @Test
        @DisplayName("Duración cero lanza excepción")
        void duracionCero_LanzaExcepcion() {
            Plan plan = planBuilder.duracionDias(0).build();

            assertThrows(PlanValidationException.class, plan::validar);
        }

        @Test
        @DisplayName("Descripción nula lanza excepción")
        void descripcionNula_LanzaExcepcion() {
            Plan plan = planBuilder.descripcion(null).build();

            PlanValidationException exception = assertThrows(
                    PlanValidationException.class,
                    plan::validar);

            assertEquals("La descripción es requerida", exception.getMessage());
        }

        @Test
        @DisplayName("Categoría muy larga lanza excepción")
        void categoriaMuyLarga_LanzaExcepcion() {
            Plan plan = planBuilder.categoria("A".repeat(51)).build();

            PlanValidationException exception = assertThrows(
                    PlanValidationException.class,
                    plan::validar);

            assertTrue(exception.getMessage().contains("categoría"));
        }
    }

    @Nested
    @DisplayName("Estado Activo/Inactivo")
    class EstadoActivoTests {

        @Test
        @DisplayName("esActivo retorna true cuando activo es true")
        void esActivo_CuandoActivo_RetornaTrue() {
            Plan plan = planBuilder.activo(true).build();

            assertTrue(plan.esActivo());
        }

        @Test
        @DisplayName("esActivo retorna false cuando activo es false")
        void esActivo_CuandoInactivo_RetornaFalse() {
            Plan plan = planBuilder.activo(false).build();

            assertFalse(plan.esActivo());
        }

        @Test
        @DisplayName("esActivo retorna false cuando activo es null")
        void esActivo_CuandoNull_RetornaFalse() {
            Plan plan = planBuilder.activo(null).build();

            assertFalse(plan.esActivo());
        }

        @Test
        @DisplayName("activar cambia estado a activo")
        void activar_CambiaEstado() {
            Plan plan = planBuilder.activo(false).build();

            plan.activar();

            assertTrue(plan.getActivo());
        }

        @Test
        @DisplayName("desactivar cambia estado a inactivo")
        void desactivar_CambiaEstado() {
            Plan plan = planBuilder.activo(true).build();

            plan.desactivar();

            assertFalse(plan.getActivo());
        }
    }

    @Nested
    @DisplayName("Cálculos de Precio")
    class CalculosPrecioTests {

        @Test
        @DisplayName("calcularPrecioMensual para plan de 30 días")
        void calcularPrecioMensual_Plan30Dias() {
            Plan plan = planBuilder
                    .precio(100.0)
                    .duracionDias(30)
                    .build();

            assertEquals(100.0, plan.calcularPrecioMensual(), 0.01);
        }

        @Test
        @DisplayName("calcularPrecioMensual para plan de 90 días")
        void calcularPrecioMensual_Plan90Dias() {
            Plan plan = planBuilder
                    .precio(270.0)
                    .duracionDias(90)
                    .build();

            // 270/90 * 30 = 90
            assertEquals(90.0, plan.calcularPrecioMensual(), 0.01);
        }

        @Test
        @DisplayName("calcularPrecioMensual con duración nula retorna precio original")
        void calcularPrecioMensual_DuracionNula_RetornaPrecio() {
            Plan plan = planBuilder
                    .precio(100.0)
                    .duracionDias(null)
                    .build();

            assertEquals(100.0, plan.calcularPrecioMensual());
        }

        @Test
        @DisplayName("calcularPrecioMensual con duración cero retorna precio original")
        void calcularPrecioMensual_DuracionCero_RetornaPrecio() {
            Plan plan = planBuilder
                    .precio(100.0)
                    .duracionDias(0)
                    .build();

            assertEquals(100.0, plan.calcularPrecioMensual());
        }
    }

    @Nested
    @DisplayName("Analytics y Rating")
    class AnalyticsTests {

        @Test
        @DisplayName("incrementarContrataciones incrementa desde 0")
        void incrementarContrataciones_DesdeNull() {
            Plan plan = planBuilder.vecesContratado(null).build();

            plan.incrementarContrataciones();

            assertEquals(1, plan.getVecesContratado());
        }

        @Test
        @DisplayName("incrementarContrataciones incrementa correctamente")
        void incrementarContrataciones_Incrementa() {
            Plan plan = planBuilder.vecesContratado(5).build();

            plan.incrementarContrataciones();

            assertEquals(6, plan.getVecesContratado());
        }

        @Test
        @DisplayName("actualizarRating primera vez")
        void actualizarRating_PrimeraVez() {
            Plan plan = planBuilder.ratingPromedio(null).build();

            plan.actualizarRating(4.5);

            assertEquals(4.5, plan.getRatingPromedio());
        }

        @Test
        @DisplayName("actualizarRating promedia con rating existente")
        void actualizarRating_PromedioExistente() {
            Plan plan = planBuilder.ratingPromedio(4.0).build();

            plan.actualizarRating(5.0);

            // (4.0 + 5.0) / 2 = 4.5
            assertEquals(4.5, plan.getRatingPromedio(), 0.01);
        }
    }
}
