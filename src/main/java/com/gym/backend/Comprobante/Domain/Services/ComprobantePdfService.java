package com.gym.backend.Comprobante.Domain.Services;

import com.gym.backend.Comprobante.Application.Dto.DatosComprobante;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComprobantePdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Colores personalizados
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(41, 128, 185); // Azul
    private static final DeviceRgb SECTION_COLOR = new DeviceRgb(236, 240, 241); // Gris claro

    /**
     * Genera un PDF del comprobante de pago
     *
     * @param datos Datos completos del comprobante
     * @return byte array del PDF generado
     */
    public byte[] generarComprobantePDF(DatosComprobante datos) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Configurar márgenes
            document.setMargins(36, 36, 36, 36);

            // Header: Información del Gimnasio
            agregarEncabezado(document, datos);

            // Separador
            document.add(new Paragraph().setMarginBottom(10));

            // Número de Comprobante
            agregarNumeroComprobante(document, datos);

            // Separador
            document.add(new Paragraph().setMarginBottom(10));

            // Fecha y Hora
            agregarFechaHora(document, datos);

            // Información del Cliente
            document.add(new Paragraph().setMarginTop(15));
            agregarInfoCliente(document, datos);

            // Información de la Membresía
            document.add(new Paragraph().setMarginTop(10));
            agregarInfoMembresia(document, datos);

            // Desglose de Costos
            document.add(new Paragraph().setMarginTop(15));
            agregarDesgloseCostos(document, datos);

            // Información del Pago
            document.add(new Paragraph().setMarginTop(15));
            agregarInfoPago(document, datos);

            // Atendido por
            document.add(new Paragraph().setMarginTop(10));
            agregarAtendidoPor(document, datos);

            // QR Code (si existe)
            if (datos.getCodigoPago() != null && !datos.getCodigoPago().isEmpty()) {
                document.add(new Paragraph().setMarginTop(15));
                agregarCodigoQR(document, datos);
            }

            // Footer
            document.add(new Paragraph().setMarginTop(20));
            agregarPiePagina(document);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar PDF del comprobante: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar el comprobante PDF", e);
        }
    }

    private void agregarEncabezado(Document document, DatosComprobante datos) throws Exception {
        Paragraph gimnasio = new Paragraph(datos.getNombreGimnasio())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(HEADER_COLOR)
                .setMarginBottom(5);
        document.add(gimnasio);

        Paragraph direccion = new Paragraph(datos.getDireccionGimnasio())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(3);
        document.add(direccion);

        Paragraph ruc = new Paragraph("RUC: " + datos.getRucGimnasio())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(3);
        document.add(ruc);

        if (datos.getTelefonoGimnasio() != null) {
            Paragraph telefono = new Paragraph("Tel: " + datos.getTelefonoGimnasio())
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(telefono);
        }

        // Línea separadora
        document.add(new Paragraph()
                .setBorder(new SolidBorder(HEADER_COLOR, 2))
                .setMarginTop(10)
                .setMarginBottom(10));
    }

    private void agregarNumeroComprobante(Document document, DatosComprobante datos) throws Exception {
        Paragraph numeroComprobante = new Paragraph("COMPROBANTE DE PAGO #" + datos.getNumeroComprobante())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(HEADER_COLOR);
        document.add(numeroComprobante);

        // Línea separadora
        document.add(new Paragraph()
                .setBorder(new SolidBorder(HEADER_COLOR, 1))
                .setMarginTop(5)
                .setMarginBottom(5));
    }

    private void agregarFechaHora(Document document, DatosComprobante datos) throws Exception {
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(
                crearCeldaSinBorde("Fecha: " + datos.getFechaEmision().format(DATE_FORMATTER), TextAlignment.LEFT));
        table.addCell(
                crearCeldaSinBorde("Hora: " + datos.getFechaEmision().format(TIME_FORMATTER), TextAlignment.RIGHT));

        document.add(table);
    }

    private void agregarInfoCliente(Document document, DatosComprobante datos) throws Exception {
        Paragraph titulo = new Paragraph("INFORMACIÓN DEL CLIENTE")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(11)
                .setMarginBottom(5);
        document.add(titulo);

        Paragraph cliente = new Paragraph("Cliente: " + datos.getNombreCliente())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(10)
                .setMarginBottom(3);
        document.add(cliente);

        Paragraph documento = new Paragraph("DNI/CI: " + datos.getDocumentoCliente())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(10);
        document.add(documento);
    }

    private void agregarInfoMembresia(Document document, DatosComprobante datos) throws Exception {
        Paragraph concepto = new Paragraph("Concepto: Membresía " + datos.getConceptoPago())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(10)
                .setMarginBottom(3);
        document.add(concepto);

        if (datos.getFechaInicio() != null && datos.getFechaFin() != null) {
            String periodo = datos.getFechaInicio().format(PERIOD_FORMATTER) + " - "
                    + datos.getFechaFin().format(PERIOD_FORMATTER);
            Paragraph periodoPar = new Paragraph("Período: " + periodo)
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(10);
            document.add(periodoPar);
        }
    }

    private void agregarDesgloseCostos(Document document, DatosComprobante datos) throws Exception {
        // Tabla con bordes
        Table table = new Table(new float[] { 3, 1 });
        table.setWidth(UnitValue.createPercentValue(100));
        table.setBackgroundColor(SECTION_COLOR);

        // Header de la tabla
        table.addCell(crearCeldaHeader("Detalle"));
        table.addCell(crearCeldaHeader("Monto"));

        // Subtotal
        table.addCell(crearCeldaDetalle("Subtotal:"));
        table.addCell(crearCeldaMonto(String.format("S/ %.2f", datos.getSubtotal())));

        // Descuento (si existe)
        if (datos.getDescuento() != null && datos.getDescuento().compareTo(BigDecimal.ZERO) > 0) {
            table.addCell(crearCeldaDetalle("Descuento:"));
            table.addCell(crearCeldaMonto(String.format("S/ %.2f", datos.getDescuento())));
        }

        // IGV
        table.addCell(crearCeldaDetalle("I.G.V (18%):"));
        table.addCell(crearCeldaMonto(String.format("S/ %.2f", datos.getIgv())));

        // Línea separadora
        Cell separador1 = new Cell(1, 2)
                .setBorder(Border.NO_BORDER)
                .setBorderTop(new SolidBorder(ColorConstants.DARK_GRAY, 1))
                .setHeight(5);
        table.addCell(separador1);

        // Total (más destacado)
        table.addCell(crearCeldaTotal("TOTAL:"));
        table.addCell(crearCeldaTotalMonto(String.format("S/ %.2f", datos.getTotal())));

        document.add(table);
    }

    private void agregarInfoPago(Document document, DatosComprobante datos) throws Exception {
        Paragraph metodoPago = new Paragraph("Método de Pago: " + datos.getMetodoPago())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(10)
                .setMarginBottom(3);
        document.add(metodoPago);

        if (datos.getCodigoOperacion() != null && !datos.getCodigoOperacion().isEmpty()) {
            Paragraph codigoOp = new Paragraph("Código de Operación: " + datos.getCodigoOperacion())
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(10);
            document.add(codigoOp);
        }
    }

    private void agregarAtendidoPor(Document document, DatosComprobante datos) throws Exception {
        Paragraph atendido = new Paragraph("Atendido por: " + datos.getEmitidoPor())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE))
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(atendido);
    }

    private void agregarCodigoQR(Document document, DatosComprobante datos) {
        try {
            // Aquí deberías generar el QR code usando la librería QR que ya tienes
            // Por ahora, solo agregamos el texto del código
            Paragraph codigoQR = new Paragraph("Código QR: " + datos.getCodigoPago())
                    .setFont(PdfFontFactory.createFont(StandardFonts.COURIER))
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(codigoQR);

            // TODO: Integrar con QrUseCase para generar el QR y agregarlo como imagen
            // byte[] qrBytes = qrUseCase.generarQRBytes(datos.getCodigoPago());
            // Image qrImage = new Image(ImageDataFactory.create(qrBytes));
            // qrImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
            // document.add(qrImage);

        } catch (Exception e) {
            log.warn("No se pudo agregar el QR al comprobante: {}", e.getMessage());
        }
    }

    private void agregarPiePagina(Document document) throws Exception {
        Paragraph gracias = new Paragraph("¡Gracias por su preferencia!")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(gracias);

        Paragraph nota = new Paragraph("Este comprobante es un documento válido como constancia de pago")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE))
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY);
        document.add(nota);
    }

    // Métodos auxiliares para crear celdas
    private Cell crearCeldaSinBorde(String texto, TextAlignment alignment) throws Exception {
        return new Cell()
                .add(new Paragraph(texto)
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                        .setFontSize(10))
                .setTextAlignment(alignment)
                .setBorder(Border.NO_BORDER);
    }

    private Cell crearCeldaHeader(String texto) throws Exception {
        return new Cell()
                .add(new Paragraph(texto)
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                        .setFontSize(10))
                .setBackgroundColor(HEADER_COLOR)
                .setFontColor(ColorConstants.WHITE)
                .setPadding(5);
    }

    private Cell crearCeldaDetalle(String texto) throws Exception {
        return new Cell()
                .add(new Paragraph(texto)
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                        .setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }

    private Cell crearCeldaMonto(String texto) throws Exception {
        return new Cell()
                .add(new Paragraph(texto)
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                        .setFontSize(10))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }

    private Cell crearCeldaTotal(String texto) throws Exception {
        return new Cell()
                .add(new Paragraph(texto)
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                        .setFontSize(12))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }

    private Cell crearCeldaTotalMonto(String texto) throws Exception {
        return new Cell()
                .add(new Paragraph(texto)
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                        .setFontSize(12))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }
}