package com.gym.backend.Comprobante.Domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad de dominio Comprobante
 */
@DisplayName("Comprobante Domain Tests")
class ComprobanteTest {

    private Comprobante.ComprobanteBuilder comprobanteBuilder;

    @BeforeEach
    void setUp() {
        // NORMALIZADO 3NF: sin usuarioId directo
        comprobanteBuilder = Comprobante.builder()
                .id(1L)
                .numeroComprobante("B001-00001")
                .pagoId(100L)
                .fechaEmision(LocalDateTime.now())
                .subtotal(new BigDecimal("84.75"))
                .igv(new BigDecimal("15.25"))
                .total(new BigDecimal("100.00"))
                .descuento(new BigDecimal("0.00"))
                .emitidoPor("admin")
                .fechaCreacion(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Creación de Comprobante")
    class CreacionTests {

        @Test
        @DisplayName("Comprobante se crea con todos los campos")
        void crearComprobante_TodosLosCampos() {
            Comprobante comprobante = comprobanteBuilder.build();

            assertNotNull(comprobante.getId());
            assertNotNull(comprobante.getNumeroComprobante());
            assertNotNull(comprobante.getPagoId());
            // ELIMINADO 3NF: usuarioId (se obtiene vía pago)
            assertNotNull(comprobante.getFechaEmision());
            assertNotNull(comprobante.getSubtotal());
            assertNotNull(comprobante.getIgv());
            assertNotNull(comprobante.getTotal());
            assertNotNull(comprobante.getDescuento());
            assertNotNull(comprobante.getEmitidoPor());
        }

        @Test
        @DisplayName("Comprobante tiene número de comprobante correcto")
        void crearComprobante_NumeroComprobante() {
            Comprobante comprobante = comprobanteBuilder
                    .numeroComprobante("B001-00123")
                    .build();

            assertEquals("B001-00123", comprobante.getNumeroComprobante());
        }
    }

    @Nested
    @DisplayName("Cálculos de Montos")
    class CalculosMontosTests {

        @Test
        @DisplayName("IGV es aproximadamente 18% del subtotal")
        void verificarIGV() {
            BigDecimal subtotal = new BigDecimal("100.00");
            BigDecimal igv = new BigDecimal("18.00");

            Comprobante comprobante = comprobanteBuilder
                    .subtotal(subtotal)
                    .igv(igv)
                    .total(new BigDecimal("118.00"))
                    .build();

            // IGV / Subtotal ≈ 0.18
            BigDecimal ratio = comprobante.getIgv().divide(comprobante.getSubtotal(), 2,
                    java.math.RoundingMode.HALF_UP);
            assertEquals(new BigDecimal("0.18"), ratio);
        }

        @Test
        @DisplayName("Total es subtotal + IGV - descuento")
        void verificarTotal() {
            BigDecimal subtotal = new BigDecimal("100.00");
            BigDecimal igv = new BigDecimal("18.00");
            BigDecimal descuento = new BigDecimal("10.00");
            BigDecimal total = subtotal.add(igv).subtract(descuento);

            Comprobante comprobante = comprobanteBuilder
                    .subtotal(subtotal)
                    .igv(igv)
                    .descuento(descuento)
                    .total(total)
                    .build();

            assertEquals(new BigDecimal("108.00"), comprobante.getTotal());
        }

        @Test
        @DisplayName("Comprobante con descuento cero")
        void comprobanteConDescuentoCero() {
            Comprobante comprobante = comprobanteBuilder
                    .descuento(BigDecimal.ZERO)
                    .build();

            assertEquals(BigDecimal.ZERO, comprobante.getDescuento());
        }
    }

    @Nested
    @DisplayName("Datos de PDF")
    class DatosPDFTests {

        @Test
        @DisplayName("Comprobante puede almacenar datos de PDF")
        void almacenarDatosPDF() {
            byte[] pdfData = "PDF_CONTENT".getBytes();

            Comprobante comprobante = comprobanteBuilder
                    .pdfData(pdfData)
                    .build();

            assertNotNull(comprobante.getPdfData());
            assertEquals(pdfData.length, comprobante.getPdfData().length);
        }

        @Test
        @DisplayName("Comprobante con pdfData null es válido")
        void pdfDataNull() {
            Comprobante comprobante = comprobanteBuilder
                    .pdfData(null)
                    .build();

            assertNull(comprobante.getPdfData());
        }
    }

    @Nested
    @DisplayName("Información de Emisión")
    class EmisionTests {

        @Test
        @DisplayName("Comprobante tiene emisor correcto")
        void emisorCorrecto() {
            Comprobante comprobante = comprobanteBuilder
                    .emitidoPor("recepcionista1")
                    .build();

            assertEquals("recepcionista1", comprobante.getEmitidoPor());
        }

        @Test
        @DisplayName("Fecha de emisión no es nula")
        void fechaEmisionNoNula() {
            Comprobante comprobante = comprobanteBuilder.build();

            assertNotNull(comprobante.getFechaEmision());
        }

        @Test
        @DisplayName("Número de comprobante tiene formato correcto")
        void formatoNumeroComprobante() {
            Comprobante comprobante = comprobanteBuilder
                    .numeroComprobante("B001-00001")
                    .build();

            assertTrue(comprobante.getNumeroComprobante().matches("^[A-Z]\\d{3}-\\d{5}$"));
        }
    }
}
