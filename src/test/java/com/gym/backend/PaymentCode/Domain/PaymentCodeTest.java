package com.gym.backend.PaymentCode.Domain;

import com.gym.backend.PaymentCode.Domain.Enums.EstadoPaymentCode;
import com.gym.backend.PaymentCode.Domain.Exceptions.PaymentCodeValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad de dominio PaymentCode
 */
@DisplayName("PaymentCode Domain Tests")
class PaymentCodeTest {

    private PaymentCode.PaymentCodeBuilder paymentCodeBuilder;

    @BeforeEach
    void setUp() {
        paymentCodeBuilder = PaymentCode.builder()
                .id(1L)
                .pagoId(100L)
                .codigo("PAY-ABC123")
                .fechaGeneracion(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusHours(24))
                .estado(EstadoPaymentCode.GENERADO)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Validación de PaymentCode")
    class ValidacionTests {

        @Test
        @DisplayName("PaymentCode válido no lanza excepción")
        void paymentCodeValido_NoLanzaExcepcion() {
            PaymentCode paymentCode = paymentCodeBuilder.build();

            assertDoesNotThrow(paymentCode::validar);
        }

        @Test
        @DisplayName("PagoId nulo lanza excepción")
        void pagoIdNulo_LanzaExcepcion() {
            PaymentCode paymentCode = paymentCodeBuilder.pagoId(null).build();

            PaymentCodeValidationException exception = assertThrows(
                    PaymentCodeValidationException.class,
                    paymentCode::validar);

            assertEquals("El ID de pago es requerido", exception.getMessage());
        }

        @Test
        @DisplayName("Código nulo lanza excepción")
        void codigoNulo_LanzaExcepcion() {
            PaymentCode paymentCode = paymentCodeBuilder.codigo(null).build();

            PaymentCodeValidationException exception = assertThrows(
                    PaymentCodeValidationException.class,
                    paymentCode::validar);

            assertEquals("El código es requerido", exception.getMessage());
        }

        @Test
        @DisplayName("Código vacío lanza excepción")
        void codigoVacio_LanzaExcepcion() {
            PaymentCode paymentCode = paymentCodeBuilder.codigo("   ").build();

            assertThrows(PaymentCodeValidationException.class, paymentCode::validar);
        }

        @Test
        @DisplayName("Fecha expiración nula lanza excepción")
        void fechaExpiracionNula_LanzaExcepcion() {
            PaymentCode paymentCode = paymentCodeBuilder.fechaExpiracion(null).build();

            PaymentCodeValidationException exception = assertThrows(
                    PaymentCodeValidationException.class,
                    paymentCode::validar);

            assertEquals("La fecha de expiración es requerida", exception.getMessage());
        }

        @Test
        @DisplayName("Fecha expiración en el pasado lanza excepción")
        void fechaExpiracionPasada_LanzaExcepcion() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .fechaExpiracion(LocalDateTime.now().minusHours(1))
                    .build();

            assertThrows(PaymentCodeValidationException.class, paymentCode::validar);
        }
    }

    @Nested
    @DisplayName("Estado de Expiración")
    class ExpiracionTests {

        @Test
        @DisplayName("estaExpirado retorna false para fecha futura")
        void estaExpirado_FechaFutura_RetornaFalse() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .fechaExpiracion(LocalDateTime.now().plusDays(1))
                    .build();

            assertFalse(paymentCode.estaExpirado());
        }

        @Test
        @DisplayName("estaExpirado retorna true para fecha pasada")
        void estaExpirado_FechaPasada_RetornaTrue() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .fechaExpiracion(LocalDateTime.now().minusMinutes(1))
                    .build();

            assertTrue(paymentCode.estaExpirado());
        }

        @Test
        @DisplayName("estaPorVencer retorna true a menos de 1 hora de expirar")
        void estaPorVencer_MenosDeUnaHora_RetornaTrue() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .fechaExpiracion(LocalDateTime.now().plusMinutes(30))
                    .build();

            assertTrue(paymentCode.estaPorVencer());
        }

        @Test
        @DisplayName("estaPorVencer retorna false con más de 1 hora")
        void estaPorVencer_MasDeUnaHora_RetornaFalse() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .fechaExpiracion(LocalDateTime.now().plusHours(2))
                    .build();

            assertFalse(paymentCode.estaPorVencer());
        }
    }

    @Nested
    @DisplayName("Uso del Código")
    class UsoCodioTests {

        @Test
        @DisplayName("puedeSerUsado retorna true para código válido no expirado")
        void puedeSerUsado_CodigoValido_RetornaTrue() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .estado(EstadoPaymentCode.GENERADO)
                    .fechaExpiracion(LocalDateTime.now().plusHours(1))
                    .build();

            assertTrue(paymentCode.puedeSerUsado());
        }

        @Test
        @DisplayName("puedeSerUsado retorna false para código usado")
        void puedeSerUsado_CodigoUsado_RetornaFalse() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .estado(EstadoPaymentCode.USADO)
                    .build();

            assertFalse(paymentCode.puedeSerUsado());
        }

        @Test
        @DisplayName("puedeSerUsado retorna false para código expirado")
        void puedeSerUsado_CodigoExpirado_RetornaFalse() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .estado(EstadoPaymentCode.GENERADO)
                    .fechaExpiracion(LocalDateTime.now().minusMinutes(1))
                    .build();

            assertFalse(paymentCode.puedeSerUsado());
        }
    }

    @Nested
    @DisplayName("Cambios de Estado")
    class CambiosEstadoTests {

        @Test
        @DisplayName("marcarComoUsado cambia estado a USADO")
        void marcarComoUsado_CambiaEstado() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .estado(EstadoPaymentCode.GENERADO)
                    .build();

            paymentCode.marcarComoUsado();

            assertEquals(EstadoPaymentCode.USADO, paymentCode.getEstado());
            assertNotNull(paymentCode.getFechaActualizacion());
        }

        @Test
        @DisplayName("marcarComoExpirado cambia estado a EXPIRADO")
        void marcarComoExpirado_CambiaEstado() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .estado(EstadoPaymentCode.GENERADO)
                    .build();

            paymentCode.marcarComoExpirado();

            assertEquals(EstadoPaymentCode.EXPIRADO, paymentCode.getEstado());
        }

        @Test
        @DisplayName("cancelar cambia estado a CANCELADO")
        void cancelar_CambiaEstado() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .estado(EstadoPaymentCode.GENERADO)
                    .build();

            paymentCode.cancelar();

            assertEquals(EstadoPaymentCode.CANCELADO, paymentCode.getEstado());
        }
    }

    @Nested
    @DisplayName("Validez del Código")
    class ValidezTests {

        @Test
        @DisplayName("esValido retorna true para código GENERADO no expirado")
        void esValido_GeneradoNoExpirado_RetornaTrue() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .estado(EstadoPaymentCode.GENERADO)
                    .fechaExpiracion(LocalDateTime.now().plusHours(1))
                    .build();

            assertTrue(paymentCode.esValido());
        }

        @Test
        @DisplayName("esValido retorna false para código USADO")
        void esValido_Usado_RetornaFalse() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .estado(EstadoPaymentCode.USADO)
                    .build();

            assertFalse(paymentCode.esValido());
        }
    }

    @Nested
    @DisplayName("Tiempo Restante")
    class TiempoRestanteTests {

        @Test
        @DisplayName("minutosRestantes retorna valor positivo para código válido")
        void minutosRestantes_CodigoValido() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .fechaExpiracion(LocalDateTime.now().plusMinutes(30))
                    .build();

            assertTrue(paymentCode.minutosRestantes() > 0);
            assertTrue(paymentCode.minutosRestantes() <= 30);
        }

        @Test
        @DisplayName("minutosRestantes retorna 0 para código expirado")
        void minutosRestantes_CodigoExpirado() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .fechaExpiracion(LocalDateTime.now().minusMinutes(1))
                    .build();

            assertEquals(0, paymentCode.minutosRestantes());
        }

        @Test
        @DisplayName("horasRestantes retorna valor positivo")
        void horasRestantes_CodigoValido() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .fechaExpiracion(LocalDateTime.now().plusHours(5))
                    .build();

            assertTrue(paymentCode.horasRestantes() > 0);
            assertTrue(paymentCode.horasRestantes() <= 5);
        }

        @Test
        @DisplayName("horasRestantes retorna 0 para código expirado")
        void horasRestantes_CodigoExpirado() {
            PaymentCode paymentCode = paymentCodeBuilder
                    .fechaExpiracion(LocalDateTime.now().minusHours(1))
                    .build();

            assertEquals(0, paymentCode.horasRestantes());
        }
    }

    @Nested
    @DisplayName("Estados de PaymentCode Enum")
    class EstadosEnumTests {

        @Test
        @DisplayName("Todos los estados están definidos")
        void todosLosEstadosExisten() {
            assertNotNull(EstadoPaymentCode.GENERADO);
            assertNotNull(EstadoPaymentCode.USADO);
            assertNotNull(EstadoPaymentCode.EXPIRADO);
            assertNotNull(EstadoPaymentCode.CANCELADO);
        }
    }
}
