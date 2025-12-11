package com.gym.backend.Pago.Domain;

import com.gym.backend.Pago.Domain.Enum.EstadoPago;
import com.gym.backend.Pago.Domain.Enum.MetodoPago;
import com.gym.backend.Pago.Domain.Exceptions.PagoValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad de dominio Pago
 */
@DisplayName("Pago Domain Tests")
class PagoTest {

    private Pago.PagoBuilder pagoBuilder;

    @BeforeEach
    void setUp() {
        pagoBuilder = Pago.builder()
                .id(1L)
                .usuarioId(100L)
                .planId(1L)
                .monto(99.99)
                .estado(EstadoPago.PENDIENTE)
                .metodoPago(MetodoPago.EFECTIVO)
                .referencia("REF-001")
                .codigoPago("PAY-001")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Validación de Pago")
    class ValidacionTests {

        @Test
        @DisplayName("Pago válido no lanza excepción")
        void pagoValido_NoLanzaExcepcion() {
            Pago pago = pagoBuilder.build();
            assertDoesNotThrow(pago::validar);
        }

        @Test
        @DisplayName("Usuario ID nulo lanza excepción")
        void usuarioIdNulo_LanzaExcepcion() {
            Pago pago = pagoBuilder.usuarioId(null).build();

            PagoValidationException exception = assertThrows(
                    PagoValidationException.class,
                    pago::validar);

            assertEquals("El ID de usuario es requerido", exception.getMessage());
        }

        @Test
        @DisplayName("Plan ID nulo lanza excepción")
        void planIdNulo_LanzaExcepcion() {
            Pago pago = pagoBuilder.planId(null).build();

            PagoValidationException exception = assertThrows(
                    PagoValidationException.class,
                    pago::validar);

            assertEquals("El ID de plan es requerido", exception.getMessage());
        }

        @Test
        @DisplayName("Monto nulo lanza excepción")
        void montoNulo_LanzaExcepcion() {
            Pago pago = pagoBuilder.monto(null).build();

            PagoValidationException exception = assertThrows(
                    PagoValidationException.class,
                    pago::validar);

            assertEquals("El monto debe ser mayor a 0", exception.getMessage());
        }

        @Test
        @DisplayName("Monto cero lanza excepción")
        void montoCero_LanzaExcepcion() {
            Pago pago = pagoBuilder.monto(0.0).build();

            assertThrows(PagoValidationException.class, pago::validar);
        }

        @Test
        @DisplayName("Monto negativo lanza excepción")
        void montoNegativo_LanzaExcepcion() {
            Pago pago = pagoBuilder.monto(-50.0).build();

            assertThrows(PagoValidationException.class, pago::validar);
        }

        @Test
        @DisplayName("Método de pago nulo lanza excepción")
        void metodoPagoNulo_LanzaExcepcion() {
            Pago pago = pagoBuilder.metodoPago(null).build();

            PagoValidationException exception = assertThrows(
                    PagoValidationException.class,
                    pago::validar);

            assertEquals("El método de pago es requerido", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Estados de Pago")
    class EstadosTests {

        @Test
        @DisplayName("esConfirmado retorna true cuando estado es CONFIRMADO")
        void esConfirmado_ConEstadoConfirmado_RetornaTrue() {
            Pago pago = pagoBuilder.estado(EstadoPago.CONFIRMADO).build();

            assertTrue(pago.esConfirmado());
        }

        @Test
        @DisplayName("esConfirmado retorna false cuando estado es PENDIENTE")
        void esConfirmado_ConEstadoPendiente_RetornaFalse() {
            Pago pago = pagoBuilder.estado(EstadoPago.PENDIENTE).build();

            assertFalse(pago.esConfirmado());
        }

        @Test
        @DisplayName("esPendiente retorna true cuando estado es PENDIENTE")
        void esPendiente_ConEstadoPendiente_RetornaTrue() {
            Pago pago = pagoBuilder.estado(EstadoPago.PENDIENTE).build();

            assertTrue(pago.esPendiente());
        }

        @Test
        @DisplayName("esPendiente retorna false cuando estado es CONFIRMADO")
        void esPendiente_ConEstadoConfirmado_RetornaFalse() {
            Pago pago = pagoBuilder.estado(EstadoPago.CONFIRMADO).build();

            assertFalse(pago.esPendiente());
        }

        @Test
        @DisplayName("puedeSerConfirmado retorna true cuando está pendiente")
        void puedeSerConfirmado_CuandoPendiente_RetornaTrue() {
            Pago pago = pagoBuilder.estado(EstadoPago.PENDIENTE).build();

            assertTrue(pago.puedeSerConfirmado());
        }

        @Test
        @DisplayName("puedeSerConfirmado retorna false cuando ya está confirmado")
        void puedeSerConfirmado_CuandoYaConfirmado_RetornaFalse() {
            Pago pago = pagoBuilder.estado(EstadoPago.CONFIRMADO).build();

            assertFalse(pago.puedeSerConfirmado());
        }
    }

    @Nested
    @DisplayName("Transiciones de Estado")
    class TransicionesTests {

        @Test
        @DisplayName("confirmar cambia estado a CONFIRMADO y establece fechaPago")
        void confirmar_CambiaEstadoYFecha() {
            Pago pago = pagoBuilder.estado(EstadoPago.PENDIENTE).build();

            pago.confirmar();

            assertEquals(EstadoPago.CONFIRMADO, pago.getEstado());
            assertNotNull(pago.getFechaPago());
            assertNotNull(pago.getFechaActualizacion());
        }

        @Test
        @DisplayName("rechazar cambia estado a RECHAZADO")
        void rechazar_CambiaEstado() {
            Pago pago = pagoBuilder.estado(EstadoPago.PENDIENTE).build();

            pago.rechazar();

            assertEquals(EstadoPago.RECHAZADO, pago.getEstado());
            assertNotNull(pago.getFechaActualizacion());
        }

        @Test
        @DisplayName("cancelar cambia estado a CANCELADO")
        void cancelar_CambiaEstado() {
            Pago pago = pagoBuilder.estado(EstadoPago.PENDIENTE).build();

            pago.cancelar();

            assertEquals(EstadoPago.CANCELADO, pago.getEstado());
            assertNotNull(pago.getFechaActualizacion());
        }
    }

    @Nested
    @DisplayName("Métodos de Pago")
    class MetodosPagoTests {

        @Test
        @DisplayName("Todos los métodos de pago están definidos")
        void todosLosMetodosExisten() {
            assertNotNull(MetodoPago.EFECTIVO);
            assertNotNull(MetodoPago.TARJETA);
            assertNotNull(MetodoPago.TRANSFERENCIA);
            assertNotNull(MetodoPago.YAPE);
            assertNotNull(MetodoPago.PLIN);
        }
    }

    @Nested
    @DisplayName("Estados de Pago Enum")
    class EstadosPagoEnumTests {

        @Test
        @DisplayName("Todos los estados de pago están definidos")
        void todosLosEstadosExisten() {
            assertNotNull(EstadoPago.PENDIENTE);
            assertNotNull(EstadoPago.CONFIRMADO);
            assertNotNull(EstadoPago.RECHAZADO);
            assertNotNull(EstadoPago.CANCELADO);
        }
    }
}
