package com.gym.backend.HistorialPagos.Domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad de dominio HistorialPago
 */
@DisplayName("HistorialPago Domain Tests")
class HistorialPagoTest {

    private HistorialPago.HistorialPagoBuilder historialBuilder;

    @BeforeEach
    void setUp() {
        // NORMALIZADO 3NF: sin usuarioId, planId, monto
        historialBuilder = HistorialPago.builder()
                .id(1L)
                .pagoId(100L)
                .estadoAnterior("PENDIENTE")
                .estadoNuevo("CONFIRMADO")
                .motivoCambio("Pago verificado")
                .usuarioModificacion("admin")
                .ipOrigen("192.168.1.100")
                .fechaCambio(LocalDateTime.now())
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Cambio de Estado")
    class CambioEstadoTests {

        @Test
        @DisplayName("esCambioDeEstado retorna true cuando estados son diferentes")
        void esCambioDeEstado_EstadosDiferentes_RetornaTrue() {
            HistorialPago historial = historialBuilder
                    .estadoAnterior("PENDIENTE")
                    .estadoNuevo("CONFIRMADO")
                    .build();

            assertTrue(historial.esCambioDeEstado());
        }

        @Test
        @DisplayName("esCambioDeEstado retorna false cuando estados son iguales")
        void esCambioDeEstado_EstadosIguales_RetornaFalse() {
            HistorialPago historial = historialBuilder
                    .estadoAnterior("PENDIENTE")
                    .estadoNuevo("PENDIENTE")
                    .build();

            assertFalse(historial.esCambioDeEstado());
        }

        @Test
        @DisplayName("esCambioDeEstado retorna false cuando estadoAnterior es null")
        void esCambioDeEstado_AnteriorNull_RetornaFalse() {
            HistorialPago historial = historialBuilder
                    .estadoAnterior(null)
                    .estadoNuevo("CONFIRMADO")
                    .build();

            assertFalse(historial.esCambioDeEstado());
        }

        @Test
        @DisplayName("esCambioDeEstado retorna false cuando estadoNuevo es null")
        void esCambioDeEstado_NuevoNull_RetornaFalse() {
            HistorialPago historial = historialBuilder
                    .estadoAnterior("PENDIENTE")
                    .estadoNuevo(null)
                    .build();

            assertFalse(historial.esCambioDeEstado());
        }
    }

    @Nested
    @DisplayName("Confirmación")
    class ConfirmacionTests {

        @Test
        @DisplayName("esConfirmacion retorna true para transición PENDIENTE->CONFIRMADO")
        void esConfirmacion_TransicionCorrecta_RetornaTrue() {
            HistorialPago historial = historialBuilder
                    .estadoAnterior("PENDIENTE")
                    .estadoNuevo("CONFIRMADO")
                    .build();

            assertTrue(historial.esConfirmacion());
        }

        @Test
        @DisplayName("esConfirmacion retorna false para otra transición")
        void esConfirmacion_OtraTransicion_RetornaFalse() {
            HistorialPago historial = historialBuilder
                    .estadoAnterior("PENDIENTE")
                    .estadoNuevo("RECHAZADO")
                    .build();

            assertFalse(historial.esConfirmacion());
        }

        @Test
        @DisplayName("esConfirmacion retorna false si estado anterior no es PENDIENTE")
        void esConfirmacion_NoEsPendiente_RetornaFalse() {
            HistorialPago historial = historialBuilder
                    .estadoAnterior("RECHAZADO")
                    .estadoNuevo("CONFIRMADO")
                    .build();

            assertFalse(historial.esConfirmacion());
        }
    }

    @Nested
    @DisplayName("Rechazo")
    class RechazoTests {

        @Test
        @DisplayName("esRechazo retorna true para transición PENDIENTE->RECHAZADO")
        void esRechazo_TransicionCorrecta_RetornaTrue() {
            HistorialPago historial = historialBuilder
                    .estadoAnterior("PENDIENTE")
                    .estadoNuevo("RECHAZADO")
                    .build();

            assertTrue(historial.esRechazo());
        }

        @Test
        @DisplayName("esRechazo retorna false para transición PENDIENTE->CONFIRMADO")
        void esRechazo_EsConfirmacion_RetornaFalse() {
            HistorialPago historial = historialBuilder
                    .estadoAnterior("PENDIENTE")
                    .estadoNuevo("CONFIRMADO")
                    .build();

            assertFalse(historial.esRechazo());
        }
    }

    @Nested
    @DisplayName("Tipo de Cambio")
    class TipoCambioTests {

        @Test
        @DisplayName("obtenerTipoCambio retorna CONFIRMACION para confirmación")
        void obtenerTipoCambio_Confirmacion() {
            HistorialPago historial = historialBuilder
                    .estadoAnterior("PENDIENTE")
                    .estadoNuevo("CONFIRMADO")
                    .build();

            assertEquals("CONFIRMACION", historial.obtenerTipoCambio());
        }

        @Test
        @DisplayName("obtenerTipoCambio retorna RECHAZO para rechazo")
        void obtenerTipoCambio_Rechazo() {
            HistorialPago historial = historialBuilder
                    .estadoAnterior("PENDIENTE")
                    .estadoNuevo("RECHAZADO")
                    .build();

            assertEquals("RECHAZO", historial.obtenerTipoCambio());
        }

        @Test
        @DisplayName("obtenerTipoCambio retorna CANCELACION para transición a cancelado")
        void obtenerTipoCambio_OtroCambio() {
            HistorialPago historial = historialBuilder
                    .estadoAnterior("CONFIRMADO")
                    .estadoNuevo("CANCELADO")
                    .build();

            // La lógica esCancelacion() retorna true para cualquier estadoNuevo =
            // "CANCELADO"
            assertEquals("CANCELACION", historial.obtenerTipoCambio());
        }

        @Test
        @DisplayName("obtenerTipoCambio retorna OTRO cuando no hay cambio de estado")
        void obtenerTipoCambio_SinCambio() {
            HistorialPago historial = historialBuilder
                    .estadoAnterior("PENDIENTE")
                    .estadoNuevo("PENDIENTE")
                    .build();

            assertEquals("OTRO", historial.obtenerTipoCambio());
        }

        @Test
        @DisplayName("obtenerTipoCambio retorna OTRO cuando estado es null")
        void obtenerTipoCambio_EstadoNull() {
            HistorialPago historial = historialBuilder
                    .estadoAnterior(null)
                    .estadoNuevo("CONFIRMADO")
                    .build();

            assertEquals("OTRO", historial.obtenerTipoCambio());
        }
    }

    @Nested
    @DisplayName("Propiedades de Historial")
    class PropiedadesTests {

        @Test
        @DisplayName("Historial se crea con todos los campos")
        void crearHistorial_TodosLosCampos() {
            HistorialPago historial = historialBuilder.build();

            assertNotNull(historial.getId());
            assertNotNull(historial.getPagoId());
            // ELIMINADOS 3NF: usuarioId, planId, monto (se obtienen del Pago)
            assertNotNull(historial.getEstadoAnterior());
            assertNotNull(historial.getEstadoNuevo());
            assertNotNull(historial.getMotivoCambio());
            assertNotNull(historial.getUsuarioModificacion());
            assertNotNull(historial.getIpOrigen());
        }

        @Test
        @DisplayName("Historial tiene información de auditoría")
        void historial_TieneAuditoria() {
            HistorialPago historial = historialBuilder
                    .usuarioModificacion("admin")
                    .ipOrigen("10.0.0.1")
                    .build();

            assertEquals("admin", historial.getUsuarioModificacion());
            assertEquals("10.0.0.1", historial.getIpOrigen());
        }
    }
}
