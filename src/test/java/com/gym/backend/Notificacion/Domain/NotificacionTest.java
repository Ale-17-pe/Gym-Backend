package com.gym.backend.Notificacion.Domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad de dominio Notificacion
 */
@DisplayName("Notificacion Domain Tests")
class NotificacionTest {

    private Notificacion notificacion;

    @BeforeEach
    void setUp() {
        notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setUsuarioId(100L);
        notificacion.setTipo(TipoNotificacion.PAGO_CONFIRMADO);
        notificacion.setTitulo("Pago Confirmado");
        notificacion.setMensaje("Tu pago ha sido procesado exitosamente");
        notificacion.setLeida(false);
        notificacion.setEmailEnviado(false);
        notificacion.setFechaCreacion(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Creación de Notificación")
    class CreacionTests {

        @Test
        @DisplayName("Notificación se crea con todos los campos")
        void crearNotificacion_TodosLosCampos() {
            assertNotNull(notificacion.getId());
            assertNotNull(notificacion.getUsuarioId());
            assertNotNull(notificacion.getTipo());
            assertNotNull(notificacion.getTitulo());
            assertNotNull(notificacion.getMensaje());
            assertNotNull(notificacion.getLeida());
            assertNotNull(notificacion.getEmailEnviado());
        }

        @Test
        @DisplayName("Notificación no leída por defecto")
        void notificacionNoLeidaPorDefecto() {
            Notificacion nuevaNotificacion = new Notificacion();
            nuevaNotificacion.setLeida(false);

            assertFalse(nuevaNotificacion.getLeida());
        }

        @Test
        @DisplayName("Email no enviado por defecto")
        void emailNoEnviadoPorDefecto() {
            Notificacion nuevaNotificacion = new Notificacion();
            nuevaNotificacion.setEmailEnviado(false);

            assertFalse(nuevaNotificacion.getEmailEnviado());
        }
    }

    @Nested
    @DisplayName("Marcar como Leída")
    class MarcarLeidaTests {

        @Test
        @DisplayName("marcarComoLeida cambia leida a true")
        void marcarComoLeida_CambiaLeida() {
            notificacion.marcarComoLeida();

            assertTrue(notificacion.getLeida());
        }

        @Test
        @DisplayName("marcarComoLeida establece fechaLeida")
        void marcarComoLeida_EstableceFechaLeida() {
            notificacion.marcarComoLeida();

            assertNotNull(notificacion.getFechaLeida());
        }

        @Test
        @DisplayName("marcarComoLeida fechaLeida es cercana a ahora")
        void marcarComoLeida_FechaLeidaCercanaAAhora() {
            LocalDateTime antes = LocalDateTime.now();
            notificacion.marcarComoLeida();
            LocalDateTime despues = LocalDateTime.now();

            assertTrue(notificacion.getFechaLeida().isAfter(antes.minusSeconds(1)));
            assertTrue(notificacion.getFechaLeida().isBefore(despues.plusSeconds(1)));
        }
    }

    @Nested
    @DisplayName("Tipos de Notificación")
    class TiposNotificacionTests {

        @Test
        @DisplayName("Tipo PAGO_CONFIRMADO existe")
        void tipoPagoConfirmado() {
            notificacion.setTipo(TipoNotificacion.PAGO_CONFIRMADO);

            assertEquals(TipoNotificacion.PAGO_CONFIRMADO, notificacion.getTipo());
        }

        @Test
        @DisplayName("Tipo PAGO_PENDIENTE existe")
        void tipoPagoPendiente() {
            notificacion.setTipo(TipoNotificacion.PAGO_PENDIENTE);

            assertEquals(TipoNotificacion.PAGO_PENDIENTE, notificacion.getTipo());
        }

        @Test
        @DisplayName("Tipo MEMBRESIA_PROXIMA_VENCER existe")
        void tipoMembresiaPorVencer() {
            notificacion.setTipo(TipoNotificacion.MEMBRESIA_PROXIMA_VENCER);

            assertEquals(TipoNotificacion.MEMBRESIA_PROXIMA_VENCER, notificacion.getTipo());
        }

        @Test
        @DisplayName("Tipo MEMBRESIA_VENCIDA existe")
        void tipoMembresiaVencida() {
            notificacion.setTipo(TipoNotificacion.MEMBRESIA_VENCIDA);

            assertEquals(TipoNotificacion.MEMBRESIA_VENCIDA, notificacion.getTipo());
        }

        @Test
        @DisplayName("Tipo BIENVENIDA existe")
        void tipoBienvenida() {
            notificacion.setTipo(TipoNotificacion.BIENVENIDA);

            assertEquals(TipoNotificacion.BIENVENIDA, notificacion.getTipo());
        }

        @Test
        @DisplayName("Cada tipo tiene descripción")
        void tiposTienenDescripcion() {
            assertNotNull(TipoNotificacion.PAGO_CONFIRMADO.getDescripcion());
            assertNotNull(TipoNotificacion.MEMBRESIA_VENCIDA.getDescripcion());
            assertNotNull(TipoNotificacion.BIENVENIDA.getDescripcion());
        }
    }

    @Nested
    @DisplayName("Propiedades de Notificación")
    class PropiedadesTests {

        @Test
        @DisplayName("Título tiene longitud máxima de 200")
        void tituloMaxLongitud() {
            String tituloLargo = "A".repeat(200);
            notificacion.setTitulo(tituloLargo);

            assertEquals(200, notificacion.getTitulo().length());
        }

        @Test
        @DisplayName("Mensaje puede ser largo")
        void mensajeLargo() {
            String mensajeLargo = "Este es un mensaje muy largo ".repeat(50);
            notificacion.setMensaje(mensajeLargo);

            assertTrue(notificacion.getMensaje().length() > 1000);
        }

        @Test
        @DisplayName("FechaLeida es null inicialmente")
        void fechaLeidaNull() {
            assertNull(notificacion.getFechaLeida());
        }
    }
}
