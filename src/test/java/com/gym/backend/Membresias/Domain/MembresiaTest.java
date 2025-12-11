package com.gym.backend.Membresias.Domain;

import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import com.gym.backend.Membresias.Domain.Exceptions.MembresiaValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad de dominio Membresia
 */
@DisplayName("Membresia Domain Tests")
class MembresiaTest {

    private Membresia.MembresiaBuilder membresiaBuilder;

    @BeforeEach
    void setUp() {
        LocalDate hoy = LocalDate.now();
        membresiaBuilder = Membresia.builder()
                .id(1L)
                .usuarioId(100L)
                .planId(1L)
                .pagoId(1L)
                .fechaInicio(hoy)
                .fechaFin(hoy.plusDays(30))
                .estado(EstadoMembresia.ACTIVA)
                .fechaCreacion(hoy)
                .fechaActualizacion(hoy);
    }

    @Nested
    @DisplayName("Validación de Membresía")
    class ValidacionTests {

        @Test
        @DisplayName("Membresía válida no lanza excepción")
        void membresiaValida_NoLanzaExcepcion() {
            Membresia membresia = membresiaBuilder.build();
            assertDoesNotThrow(membresia::validar);
        }

        @Test
        @DisplayName("Usuario ID nulo lanza excepción")
        void usuarioIdNulo_LanzaExcepcion() {
            Membresia membresia = membresiaBuilder.usuarioId(null).build();

            MembresiaValidationException exception = assertThrows(
                    MembresiaValidationException.class,
                    membresia::validar);

            assertEquals("El ID de usuario es requerido", exception.getMessage());
        }

        @Test
        @DisplayName("Plan ID nulo lanza excepción")
        void planIdNulo_LanzaExcepcion() {
            Membresia membresia = membresiaBuilder.planId(null).build();

            MembresiaValidationException exception = assertThrows(
                    MembresiaValidationException.class,
                    membresia::validar);

            assertEquals("El ID de plan es requerido", exception.getMessage());
        }

        @Test
        @DisplayName("Pago ID nulo lanza excepción")
        void pagoIdNulo_LanzaExcepcion() {
            Membresia membresia = membresiaBuilder.pagoId(null).build();

            MembresiaValidationException exception = assertThrows(
                    MembresiaValidationException.class,
                    membresia::validar);

            assertEquals("El ID de pago es requerido", exception.getMessage());
        }

        @Test
        @DisplayName("Fecha inicio nula lanza excepción")
        void fechaInicioNula_LanzaExcepcion() {
            Membresia membresia = membresiaBuilder.fechaInicio(null).build();

            MembresiaValidationException exception = assertThrows(
                    MembresiaValidationException.class,
                    membresia::validar);

            assertEquals("La fecha de inicio es requerida", exception.getMessage());
        }

        @Test
        @DisplayName("Fecha fin nula lanza excepción")
        void fechaFinNula_LanzaExcepcion() {
            Membresia membresia = membresiaBuilder.fechaFin(null).build();

            MembresiaValidationException exception = assertThrows(
                    MembresiaValidationException.class,
                    membresia::validar);

            assertEquals("La fecha de fin es requerida", exception.getMessage());
        }

        @Test
        @DisplayName("Fecha fin anterior a fecha inicio lanza excepción")
        void fechaFinAnteriorAInicio_LanzaExcepcion() {
            LocalDate hoy = LocalDate.now();
            Membresia membresia = membresiaBuilder
                    .fechaInicio(hoy)
                    .fechaFin(hoy.minusDays(1))
                    .build();

            MembresiaValidationException exception = assertThrows(
                    MembresiaValidationException.class,
                    membresia::validar);

            assertTrue(exception.getMessage().contains("fecha de fin"));
        }
    }

    @Nested
    @DisplayName("Estado de Membresía")
    class EstadoTests {

        @Test
        @DisplayName("estaActiva retorna true para membresía activa dentro del período")
        void estaActiva_MembresiaActiva_RetornaTrue() {
            LocalDate hoy = LocalDate.now();
            Membresia membresia = membresiaBuilder
                    .fechaInicio(hoy.minusDays(5))
                    .fechaFin(hoy.plusDays(25))
                    .estado(EstadoMembresia.ACTIVA)
                    .build();

            assertTrue(membresia.estaActiva());
        }

        @Test
        @DisplayName("estaActiva retorna false para membresía vencida")
        void estaActiva_MembresiaVencida_RetornaFalse() {
            LocalDate hoy = LocalDate.now();
            Membresia membresia = membresiaBuilder
                    .fechaInicio(hoy.minusDays(60))
                    .fechaFin(hoy.minusDays(30))
                    .estado(EstadoMembresia.ACTIVA)
                    .build();

            assertFalse(membresia.estaActiva());
        }

        @Test
        @DisplayName("estaActiva retorna false para membresía suspendida")
        void estaActiva_MembresiaSuspendida_RetornaFalse() {
            LocalDate hoy = LocalDate.now();
            Membresia membresia = membresiaBuilder
                    .fechaInicio(hoy.minusDays(5))
                    .fechaFin(hoy.plusDays(25))
                    .estado(EstadoMembresia.SUSPENDIDA)
                    .build();

            assertFalse(membresia.estaActiva());
        }

        @Test
        @DisplayName("estaVencida retorna true después de fecha fin")
        void estaVencida_DespuesDeFechaFin_RetornaTrue() {
            LocalDate hoy = LocalDate.now();
            Membresia membresia = membresiaBuilder
                    .fechaFin(hoy.minusDays(1))
                    .build();

            assertTrue(membresia.estaVencida());
        }

        @Test
        @DisplayName("estaVencida retorna false antes de fecha fin")
        void estaVencida_AntesDeFechaFin_RetornaFalse() {
            LocalDate hoy = LocalDate.now();
            Membresia membresia = membresiaBuilder
                    .fechaFin(hoy.plusDays(10))
                    .build();

            assertFalse(membresia.estaVencida());
        }

        @Test
        @DisplayName("estaPorVencer retorna true a 7 días o menos de vencer")
        void estaPorVencer_A7DiasDeVencer_RetornaTrue() {
            LocalDate hoy = LocalDate.now();
            Membresia membresia = membresiaBuilder
                    .fechaInicio(hoy.minusDays(23))
                    .fechaFin(hoy.plusDays(7))
                    .estado(EstadoMembresia.ACTIVA)
                    .build();

            assertTrue(membresia.estaPorVencer());
        }

        @Test
        @DisplayName("estaPorVencer retorna false con más de 7 días")
        void estaPorVencer_MasDe7Dias_RetornaFalse() {
            LocalDate hoy = LocalDate.now();
            Membresia membresia = membresiaBuilder
                    .fechaInicio(hoy.minusDays(5))
                    .fechaFin(hoy.plusDays(25))
                    .estado(EstadoMembresia.ACTIVA)
                    .build();

            assertFalse(membresia.estaPorVencer());
        }
    }

    @Nested
    @DisplayName("Transiciones de Estado")
    class TransicionesTests {

        @Test
        @DisplayName("activar cambia estado a ACTIVA")
        void activar_CambiaEstado() {
            Membresia membresia = membresiaBuilder
                    .estado(EstadoMembresia.SUSPENDIDA)
                    .build();

            membresia.activar();

            assertEquals(EstadoMembresia.ACTIVA, membresia.getEstado());
            assertNotNull(membresia.getFechaActualizacion());
        }

        @Test
        @DisplayName("vencer cambia estado a VENCIDA")
        void vencer_CambiaEstado() {
            Membresia membresia = membresiaBuilder
                    .estado(EstadoMembresia.ACTIVA)
                    .build();

            membresia.vencer();

            assertEquals(EstadoMembresia.VENCIDA, membresia.getEstado());
        }

        @Test
        @DisplayName("suspender cambia estado a SUSPENDIDA")
        void suspender_CambiaEstado() {
            Membresia membresia = membresiaBuilder
                    .estado(EstadoMembresia.ACTIVA)
                    .build();

            membresia.suspender();

            assertEquals(EstadoMembresia.SUSPENDIDA, membresia.getEstado());
        }

        @Test
        @DisplayName("cancelar cambia estado a CANCELADA")
        void cancelar_CambiaEstado() {
            Membresia membresia = membresiaBuilder
                    .estado(EstadoMembresia.ACTIVA)
                    .build();

            membresia.cancelar();

            assertEquals(EstadoMembresia.CANCELADA, membresia.getEstado());
        }
    }

    @Nested
    @DisplayName("Cálculos de Días")
    class CalculosDiasTests {

        @Test
        @DisplayName("diasRestantes calcula correctamente")
        void diasRestantes_CalculaCorrectamente() {
            LocalDate hoy = LocalDate.now();
            Membresia membresia = membresiaBuilder
                    .fechaFin(hoy.plusDays(15))
                    .build();

            assertEquals(15, membresia.diasRestantes());
        }

        @Test
        @DisplayName("diasRestantes retorna 0 para membresía vencida")
        void diasRestantes_MembresiaVencida_RetornaCero() {
            LocalDate hoy = LocalDate.now();
            Membresia membresia = membresiaBuilder
                    .fechaFin(hoy.minusDays(5))
                    .build();

            assertEquals(0, membresia.diasRestantes());
        }

        @Test
        @DisplayName("diasTranscurridos calcula correctamente")
        void diasTranscurridos_CalculaCorrectamente() {
            LocalDate hoy = LocalDate.now();
            Membresia membresia = membresiaBuilder
                    .fechaInicio(hoy.minusDays(10))
                    .fechaFin(hoy.plusDays(20))
                    .build();

            assertEquals(10, membresia.diasTranscurridos());
        }

        @Test
        @DisplayName("diasTranscurridos retorna 0 si fecha inicio es futura")
        void diasTranscurridos_FechaFutura_RetornaCero() {
            LocalDate hoy = LocalDate.now();
            Membresia membresia = membresiaBuilder
                    .fechaInicio(hoy.plusDays(5))
                    .fechaFin(hoy.plusDays(35))
                    .build();

            assertEquals(0, membresia.diasTranscurridos());
        }

        @Test
        @DisplayName("porcentajeUso calcula correctamente")
        void porcentajeUso_CalculaCorrectamente() {
            LocalDate hoy = LocalDate.now();
            Membresia membresia = membresiaBuilder
                    .fechaInicio(hoy.minusDays(15))
                    .fechaFin(hoy.plusDays(15))
                    .build();

            double porcentaje = membresia.porcentajeUso();
            assertEquals(50.0, porcentaje, 0.1);
        }
    }

    @Nested
    @DisplayName("Estados de Membresía Enum")
    class EstadosMembresiaEnumTests {

        @Test
        @DisplayName("Todos los estados están definidos")
        void todosLosEstadosExisten() {
            assertNotNull(EstadoMembresia.ACTIVA);
            assertNotNull(EstadoMembresia.SUSPENDIDA);
            assertNotNull(EstadoMembresia.VENCIDA);
            assertNotNull(EstadoMembresia.CANCELADA);
        }
    }
}
