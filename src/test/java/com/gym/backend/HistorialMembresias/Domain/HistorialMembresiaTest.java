package com.gym.backend.HistorialMembresias.Domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad de dominio HistorialMembresia
 */
@DisplayName("HistorialMembresia Domain Tests")
class HistorialMembresiaTest {

    private HistorialMembresia.HistorialMembresiaBuilder historialBuilder;

    @BeforeEach
    void setUp() {
        // NORMALIZADO 3NF: sin usuarioId, planId
        historialBuilder = HistorialMembresia.builder()
                .id(1L)
                .membresiaId(100L)
                .accion("CREAR")
                .estadoAnterior(null)
                .estadoNuevo("ACTIVA")
                .motivoCambio("Creación de nueva membresía")
                .usuarioModificacion("admin")
                .ipOrigen("192.168.1.100")
                .fechaCambio(LocalDateTime.now())
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Tipos de Acción")
    class TiposAccionTests {

        @Test
        @DisplayName("esCreacion retorna true para acción CREAR")
        void esCreacion_AccionCrear_RetornaTrue() {
            HistorialMembresia historial = historialBuilder.accion("CREAR").build();

            assertTrue(historial.esCreacion());
        }

        @Test
        @DisplayName("esExtension retorna true para acción EXTENDER")
        void esExtension_AccionExtender_RetornaTrue() {
            HistorialMembresia historial = historialBuilder.accion("EXTENDER").build();

            assertTrue(historial.esExtension());
        }

        @Test
        @DisplayName("esSuspension retorna true para acción SUSPENDER")
        void esSuspension_AccionSuspender_RetornaTrue() {
            HistorialMembresia historial = historialBuilder.accion("SUSPENDER").build();

            assertTrue(historial.esSuspension());
        }

        @Test
        @DisplayName("esReactivacion retorna true para acción REACTIVAR")
        void esReactivacion_AccionReactivar_RetornaTrue() {
            HistorialMembresia historial = historialBuilder.accion("REACTIVAR").build();

            assertTrue(historial.esReactivacion());
        }

        @Test
        @DisplayName("esCancelacion retorna true para acción CANCELAR")
        void esCancelacion_AccionCancelar_RetornaTrue() {
            HistorialMembresia historial = historialBuilder.accion("CANCELAR").build();

            assertTrue(historial.esCancelacion());
        }

        @Test
        @DisplayName("esVencimiento retorna true para acción VENCER")
        void esVencimiento_AccionVencer_RetornaTrue() {
            HistorialMembresia historial = historialBuilder.accion("VENCER").build();

            assertTrue(historial.esVencimiento());
        }
    }

    @Nested
    @DisplayName("Métodos de Verificación Negativos")
    class VerificacionNegativaTests {

        @Test
        @DisplayName("esCreacion retorna false para otra acción")
        void esCreacion_OtraAccion_RetornaFalse() {
            HistorialMembresia historial = historialBuilder.accion("SUSPENDER").build();

            assertFalse(historial.esCreacion());
        }

        @Test
        @DisplayName("esExtension retorna false para otra acción")
        void esExtension_OtraAccion_RetornaFalse() {
            HistorialMembresia historial = historialBuilder.accion("CANCELAR").build();

            assertFalse(historial.esExtension());
        }

        @Test
        @DisplayName("esSuspension retorna false para otra acción")
        void esSuspension_OtraAccion_RetornaFalse() {
            HistorialMembresia historial = historialBuilder.accion("CREAR").build();

            assertFalse(historial.esSuspension());
        }

        @Test
        @DisplayName("esVencimiento retorna false para otra acción")
        void esVencimiento_OtraAccion_RetornaFalse() {
            HistorialMembresia historial = historialBuilder.accion("REACTIVAR").build();

            assertFalse(historial.esVencimiento());
        }
    }

    @Nested
    @DisplayName("Obtener Tipo de Cambio")
    class TipoCambioTests {

        @Test
        @DisplayName("obtenerTipoCambio retorna la acción")
        void obtenerTipoCambio_RetornaAccion() {
            HistorialMembresia historial = historialBuilder.accion("EXTENDER").build();

            assertEquals("EXTENDER", historial.obtenerTipoCambio());
        }

        @Test
        @DisplayName("obtenerTipoCambio para CREAR")
        void obtenerTipoCambio_Crear() {
            HistorialMembresia historial = historialBuilder.accion("CREAR").build();

            assertEquals("CREAR", historial.obtenerTipoCambio());
        }

        @Test
        @DisplayName("obtenerTipoCambio para VENCER")
        void obtenerTipoCambio_Vencer() {
            HistorialMembresia historial = historialBuilder.accion("VENCER").build();

            assertEquals("VENCER", historial.obtenerTipoCambio());
        }
    }

    @Nested
    @DisplayName("Propiedades de Historial")
    class PropiedadesTests {

        @Test
        @DisplayName("Historial se crea con todos los campos")
        void crearHistorial_TodosLosCampos() {
            HistorialMembresia historial = historialBuilder.build();

            assertNotNull(historial.getId());
            assertNotNull(historial.getMembresiaId());
            // ELIMINADOS 3NF: usuarioId, planId (se obtienen de la Membresia)
            assertNotNull(historial.getAccion());
            assertNotNull(historial.getEstadoNuevo());
            assertNotNull(historial.getMotivoCambio());
            assertNotNull(historial.getUsuarioModificacion());
            assertNotNull(historial.getIpOrigen());
        }

        @Test
        @DisplayName("Estado anterior puede ser null para creación")
        void estadoAnterior_NuloParaCreacion() {
            HistorialMembresia historial = historialBuilder
                    .accion("CREAR")
                    .estadoAnterior(null)
                    .build();

            assertNull(historial.getEstadoAnterior());
            assertTrue(historial.esCreacion());
        }

        @Test
        @DisplayName("Historial tiene información de auditoría")
        void historial_TieneAuditoria() {
            HistorialMembresia historial = historialBuilder
                    .usuarioModificacion("sistema")
                    .ipOrigen("127.0.0.1")
                    .build();

            assertEquals("sistema", historial.getUsuarioModificacion());
            assertEquals("127.0.0.1", historial.getIpOrigen());
        }
    }

    @Nested
    @DisplayName("Transiciones de Estado")
    class TransicionesTests {

        @Test
        @DisplayName("Transición ACTIVA -> SUSPENDIDA")
        void transicionActivaASuspendida() {
            HistorialMembresia historial = historialBuilder
                    .accion("SUSPENDER")
                    .estadoAnterior("ACTIVA")
                    .estadoNuevo("SUSPENDIDA")
                    .build();

            assertTrue(historial.esSuspension());
            assertEquals("ACTIVA", historial.getEstadoAnterior());
            assertEquals("SUSPENDIDA", historial.getEstadoNuevo());
        }

        @Test
        @DisplayName("Transición SUSPENDIDA -> ACTIVA")
        void transicionSuspendidaAActiva() {
            HistorialMembresia historial = historialBuilder
                    .accion("REACTIVAR")
                    .estadoAnterior("SUSPENDIDA")
                    .estadoNuevo("ACTIVA")
                    .build();

            assertTrue(historial.esReactivacion());
            assertEquals("SUSPENDIDA", historial.getEstadoAnterior());
            assertEquals("ACTIVA", historial.getEstadoNuevo());
        }

        @Test
        @DisplayName("Transición ACTIVA -> VENCIDA")
        void transicionActivaAVencida() {
            HistorialMembresia historial = historialBuilder
                    .accion("VENCER")
                    .estadoAnterior("ACTIVA")
                    .estadoNuevo("VENCIDA")
                    .build();

            assertTrue(historial.esVencimiento());
            assertEquals("ACTIVA", historial.getEstadoAnterior());
            assertEquals("VENCIDA", historial.getEstadoNuevo());
        }
    }
}
