package com.gym.backend.Reportes.Application.Service;

import com.gym.backend.Reportes.Application.DTO.*;
import com.gym.backend.Reportes.Domain.Record.*;
import com.gym.backend.Reportes.Domain.ReportesRepositoryPort;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportesExportService {

    private final ReportesRepositoryPort repositoryPort;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ========================================
    // INGRESOS DETALLADOS
    // ========================================

    public List<IngresoDetalladoDTO> obtenerIngresosDetallados(FiltroIngresoDTO filtro) {
        log.info("Obteniendo ingresos detallados con filtro: {}", filtro);
        return repositoryPort.obtenerIngresosDetallados(filtro);
    }

    public ResumenIngresoDTO obtenerResumenIngresos(FiltroIngresoDTO filtro) {
        log.info("Obteniendo resumen de ingresos con filtro: {}", filtro);
        return repositoryPort.obtenerResumenIngresos(filtro);
    }

    public byte[] generarIngresosPDF(FiltroIngresoDTO filtro) {
        log.info("Generando PDF de ingresos detallados");
        List<IngresoDetalladoDTO> ingresos = obtenerIngresosDetallados(filtro);
        ResumenIngresoDTO resumen = obtenerResumenIngresos(filtro);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            // Colores corporativos (Negro/Gris Oscuro y Amarillo Dorado)
            java.awt.Color goldColor = new java.awt.Color(255, 193, 7); // Un dorado más vibrante
            java.awt.Color darkHeader = new java.awt.Color(33, 37, 41); // Gris oscuro casi negro

            // Título
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(
                    com.lowagie.text.Font.HELVETICA, 20, com.lowagie.text.Font.BOLD, darkHeader);
            Paragraph title = new Paragraph("Reporte Detallado de Ingresos", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Subtítulo con fechas si existen
            if (filtro.getFechaInicio() != null && filtro.getFechaFin() != null) {
                com.lowagie.text.Font dateFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10,
                        com.lowagie.text.Font.NORMAL, java.awt.Color.GRAY);
                String rango = "Desde: " + filtro.getFechaInicio().format(DATE_FORMATTER) +
                        "   Hasta: " + filtro.getFechaFin().format(DATE_FORMATTER);
                Paragraph rangoP = new Paragraph(rango, dateFont);
                rangoP.setAlignment(Element.ALIGN_CENTER);
                rangoP.setSpacingAfter(20);
                document.add(rangoP);
            }

            // Resumen Cards (Tabla simulando cards)
            PdfPTable resumenTable = new PdfPTable(4);
            resumenTable.setWidthPercentage(100);
            resumenTable.setSpacingAfter(30);

            // Estilos para headers de resumen
            com.lowagie.text.Font resHeaderFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10,
                    com.lowagie.text.Font.BOLD, java.awt.Color.WHITE);
            // Estilos para valores de resumen
            com.lowagie.text.Font resValueFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14,
                    com.lowagie.text.Font.BOLD, darkHeader);

            // Helper para celdas de resumen
            agregarCeldaResumen(resumenTable, "Total Confirmado", resumen.getTotalConfirmado(),
                    new java.awt.Color(40, 167, 69), resHeaderFont, resValueFont); // Verde
            agregarCeldaResumen(resumenTable, "Total Pendiente", resumen.getTotalPendiente(),
                    new java.awt.Color(255, 193, 7), resHeaderFont, resValueFont); // Amarillo
            agregarCeldaResumen(resumenTable, "Total Cancelado", resumen.getTotalCancelado(),
                    new java.awt.Color(220, 53, 69), resHeaderFont, resValueFont); // Rojo
            agregarCeldaResumen(resumenTable, "Total Generado", resumen.getTotalGeneral(), darkHeader, resHeaderFont,
                    resValueFont); // Oscuro

            document.add(resumenTable);

            // Tabla de detalles
            com.lowagie.text.Font tableHeaderFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10,
                    com.lowagie.text.Font.BOLD, java.awt.Color.BLACK);

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 2.5f, 2, 3.5f, 2.5f, 2, 2, 2, 2 });
            table.setHeaderRows(1);

            // Headers con fondo amarillo dorado
            String[] headers = { "Fecha", "DNI", "Usuario", "Plan", "Monto", "Método", "Estado", "Código" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, tableHeaderFont));
                cell.setBackgroundColor(goldColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(8);
                cell.setBorderColor(java.awt.Color.LIGHT_GRAY);
                table.addCell(cell);
            }

            // Datos
            com.lowagie.text.Font rowFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9,
                    com.lowagie.text.Font.NORMAL);
            boolean alternate = false;
            java.awt.Color lightGrayObj = new java.awt.Color(248, 249, 250);

            for (IngresoDetalladoDTO ingreso : ingresos) {
                alternate = !alternate;
                java.awt.Color bg = alternate ? lightGrayObj : java.awt.Color.WHITE;

                agregarCeldaTabla(table, ingreso.getFechaPago().format(DATE_FORMATTER), rowFont, bg);
                agregarCeldaTabla(table, ingreso.getUsuarioDni(), rowFont, bg);
                agregarCeldaTabla(table, ingreso.getUsuarioNombre(), rowFont, bg);
                agregarCeldaTabla(table, ingreso.getPlanNombre(), rowFont, bg);
                agregarCeldaTabla(table, "S/. " + String.format("%.2f", ingreso.getMonto()), rowFont, bg);
                agregarCeldaTabla(table, ingreso.getMetodoPago(), rowFont, bg);

                // Color para estado
                com.lowagie.text.Font stateFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8,
                        com.lowagie.text.Font.BOLD);
                if ("CONFIRMADO".equals(ingreso.getEstado()))
                    stateFont.setColor(new java.awt.Color(40, 167, 69));
                else if ("PENDIENTE".equals(ingreso.getEstado()))
                    stateFont.setColor(new java.awt.Color(255, 193, 7)); // Darker yellow for text
                else
                    stateFont.setColor(new java.awt.Color(220, 53, 69));

                PdfPCell stateCell = new PdfPCell(new Phrase(ingreso.getEstado(), stateFont));
                stateCell.setBackgroundColor(bg);
                stateCell.setPadding(6);
                stateCell.setBorderColor(java.awt.Color.LIGHT_GRAY);
                table.addCell(stateCell);

                agregarCeldaTabla(table, ingreso.getCodigoPago(), rowFont, bg);
            }

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generando PDF de ingresos", e);
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    private void agregarCeldaResumen(PdfPTable table, String titulo, Double valor, java.awt.Color bgColor,
            com.lowagie.text.Font headerFont, com.lowagie.text.Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        cell.setPadding(5);

        PdfPTable inner = new PdfPTable(1);
        inner.setWidthPercentage(100);

        PdfPCell titleCell = new PdfPCell(new Phrase(titulo, headerFont));
        titleCell.setBackgroundColor(bgColor);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setPadding(6);
        titleCell.setBorderColor(bgColor);

        PdfPCell valueCell = new PdfPCell(new Phrase("S/. " + String.format("%.2f", valor), valueFont));
        valueCell.setBackgroundColor(new java.awt.Color(248, 249, 250)); // Gris muy claro
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setPadding(10);
        valueCell.setBorderColor(bgColor); // Borde del color del header
        valueCell.setBorderWidth(1);

        inner.addCell(titleCell);
        inner.addCell(valueCell);

        cell.addElement(inner);
        table.addCell(cell);
    }

    private void agregarCeldaTabla(PdfPTable table, String text, com.lowagie.text.Font font, java.awt.Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(java.awt.Color.LIGHT_GRAY);
        table.addCell(cell);
    }

    public byte[] generarIngresosExcel(FiltroIngresoDTO filtro) {
        log.info("Generando Excel de ingresos detallados");
        List<IngresoDetalladoDTO> ingresos = obtenerIngresosDetallados(filtro);
        ResumenIngresoDTO resumen = obtenerResumenIngresos(filtro);

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // Hoja 1: Resumen
            Sheet resumenSheet = workbook.createSheet("Resumen");
            crearHojaResumen(resumenSheet, resumen, workbook);

            // Hoja 2: Detalle
            Sheet detalleSheet = workbook.createSheet("Detalle");
            crearHojaDetalleIngresos(detalleSheet, ingresos, workbook);

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generando Excel de ingresos", e);
            throw new RuntimeException("Error generando Excel", e);
        }
    }

    // ========================================
    // PAGOS POR MÉTODO
    // ========================================

    public byte[] generarPagosMetodoPDF() {
        log.info("Generando PDF de pagos por método");
        List<PagosPorMetodo> datos = repositoryPort.pagosPorMetodo();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Título
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(
                    com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD, new java.awt.Color(255, 215, 0));
            Paragraph title = new Paragraph("Pagos por Método de Pago", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Tabla
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(80);
            table.setWidths(new float[] { 3, 2, 2 });

            agregarCeldaHeader(table, "Método de Pago");
            agregarCeldaHeader(table, "Cantidad");
            agregarCeldaHeader(table, "Total (S/.)");

            double totalGeneral = 0.0;
            for (PagosPorMetodo dato : datos) {
                agregarCelda(table, dato.metodoPago());
                agregarCelda(table, String.valueOf(dato.cantidad()));
                agregarCelda(table, String.format("%.2f", dato.total()));
                totalGeneral += dato.total();
            }

            // Footer con total
            com.lowagie.text.Font boldFont = new com.lowagie.text.Font(
                    com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD);
            PdfPCell cellFooter = new PdfPCell(new Phrase("TOTAL GENERAL", boldFont));
            cellFooter.setColspan(2);
            cellFooter.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellFooter.setBackgroundColor(new java.awt.Color(245, 245, 245));
            table.addCell(cellFooter);

            PdfPCell cellTotal = new PdfPCell(new Phrase(String.format("S/. %.2f", totalGeneral), boldFont));
            cellTotal.setBackgroundColor(new java.awt.Color(255, 215, 0));
            table.addCell(cellTotal);

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generando PDF de pagos por método", e);
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    public byte[] generarPagosMetodoExcel() {
        log.info("Generando Excel de pagos por método");
        List<PagosPorMetodo> datos = repositoryPort.pagosPorMetodo();

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Pagos por Método");

            // Estilos
            CellStyle headerStyle = crearEstiloHeader(workbook);
            CellStyle dataStyle = crearEstiloData(workbook);

            // Header
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            crearCelda(headerRow, 0, "Método de Pago", headerStyle);
            crearCelda(headerRow, 1, "Cantidad", headerStyle);
            crearCelda(headerRow, 2, "Total (S/.)", headerStyle);

            // Datos
            int rowNum = 1;
            double totalGeneral = 0.0;
            for (PagosPorMetodo dato : datos) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                crearCelda(row, 0, dato.metodoPago(), dataStyle);
                crearCelda(row, 1, String.valueOf(dato.cantidad()), dataStyle);
                crearCelda(row, 2, String.format("%.2f", dato.total()), dataStyle);
                totalGeneral += dato.total();
            }

            // Total
            org.apache.poi.ss.usermodel.Row totalRow = sheet.createRow(rowNum);
            crearCelda(totalRow, 0, "TOTAL GENERAL", headerStyle);
            crearCelda(totalRow, 1, "", headerStyle);
            crearCelda(totalRow, 2, String.format("%.2f", totalGeneral), headerStyle);

            // Auto-ajustar columnas
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generando Excel de pagos por método", e);
            throw new RuntimeException("Error generando Excel", e);
        }
    }

    // ========================================
    // USUARIOS NUEVOS
    // ========================================

    public byte[] generarUsuariosNuevosPDF() {
        log.info("Generando PDF de usuarios nuevos");
        List<UsuariosNuevos> datos = repositoryPort.usuariosNuevosPorMes();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(
                    com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD, new java.awt.Color(255, 215, 0));
            Paragraph title = new Paragraph("Usuarios Nuevos por Mes", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(70);
            table.setWidths(new float[] { 2, 2, 2 });

            agregarCeldaHeader(table, "Mes");
            agregarCeldaHeader(table, "Año");
            agregarCeldaHeader(table, "Cantidad");

            int total = 0;
            for (UsuariosNuevos dato : datos) {
                agregarCelda(table, String.valueOf(dato.mes()));
                agregarCelda(table, String.valueOf(dato.anio()));
                agregarCelda(table, String.valueOf(dato.cantidad()));
                total += dato.cantidad();
            }

            // Total
            com.lowagie.text.Font boldFont = new com.lowagie.text.Font(
                    com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD);
            PdfPCell cellFooter = new PdfPCell(new Phrase("TOTAL", boldFont));
            cellFooter.setColspan(2);
            cellFooter.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellFooter.setBackgroundColor(new java.awt.Color(245, 245, 245));
            table.addCell(cellFooter);

            PdfPCell cellTotal = new PdfPCell(new Phrase(String.valueOf(total), boldFont));
            cellTotal.setBackgroundColor(new java.awt.Color(255, 215, 0));
            table.addCell(cellTotal);

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generando PDF de usuarios nuevos", e);
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    public byte[] generarUsuariosNuevosExcel() {
        log.info("Generando Excel de usuarios nuevos");
        List<UsuariosNuevos> datos = repositoryPort.usuariosNuevosPorMes();

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Usuarios Nuevos");
            CellStyle headerStyle = crearEstiloHeader(workbook);
            CellStyle dataStyle = crearEstiloData(workbook);

            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            crearCelda(headerRow, 0, "Mes", headerStyle);
            crearCelda(headerRow, 1, "Año", headerStyle);
            crearCelda(headerRow, 2, "Cantidad", headerStyle);

            int rowNum = 1;
            int total = 0;
            for (UsuariosNuevos dato : datos) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                crearCelda(row, 0, String.valueOf(dato.mes()), dataStyle);
                crearCelda(row, 1, String.valueOf(dato.anio()), dataStyle);
                crearCelda(row, 2, String.valueOf(dato.cantidad()), dataStyle);
                total += dato.cantidad();
            }

            org.apache.poi.ss.usermodel.Row totalRow = sheet.createRow(rowNum);
            crearCelda(totalRow, 0, "TOTAL", headerStyle);
            crearCelda(totalRow, 1, "", headerStyle);
            crearCelda(totalRow, 2, String.valueOf(total), headerStyle);

            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generando Excel de usuarios nuevos", e);
            throw new RuntimeException("Error generando Excel", e);
        }
    }

    // ========================================
    // TOP PLANES
    // ========================================

    public byte[] generarTopPlanesPDF() {
        log.info("Generando PDF de top planes");
        List<TopPlanes> datos = repositoryPort.topPlanes();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(
                    com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD, new java.awt.Color(255, 215, 0));
            Paragraph title = new Paragraph("Top Planes Más Vendidos", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(80);
            table.setWidths(new float[] { 4, 2, 2 });

            agregarCeldaHeader(table, "Plan");
            agregarCeldaHeader(table, "Cantidad Vendida");
            agregarCeldaHeader(table, "Total Ingresos (S/.)");

            double totalIngresos = 0.0;
            for (TopPlanes dato : datos) {
                agregarCelda(table, dato.planNombre());
                agregarCelda(table, String.valueOf(dato.cantidad()));
                agregarCelda(table, String.format("%.2f", dato.totalIngresos()));
                totalIngresos += dato.totalIngresos();
            }

            com.lowagie.text.Font boldFont = new com.lowagie.text.Font(
                    com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD);
            PdfPCell cellFooter = new PdfPCell(new Phrase("TOTAL GENERAL", boldFont));
            cellFooter.setColspan(2);
            cellFooter.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellFooter.setBackgroundColor(new java.awt.Color(245, 245, 245));
            table.addCell(cellFooter);

            PdfPCell cellTotal = new PdfPCell(new Phrase(String.format("S/. %.2f", totalIngresos), boldFont));
            cellTotal.setBackgroundColor(new java.awt.Color(255, 215, 0));
            table.addCell(cellTotal);

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generando PDF de top planes", e);
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    public byte[] generarTopPlanesExcel() {
        log.info("Generando Excel de top planes");
        List<TopPlanes> datos = repositoryPort.topPlanes();

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Top Planes");
            CellStyle headerStyle = crearEstiloHeader(workbook);
            CellStyle dataStyle = crearEstiloData(workbook);

            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            crearCelda(headerRow, 0, "Plan", headerStyle);
            crearCelda(headerRow, 1, "Cantidad Vendida", headerStyle);
            crearCelda(headerRow, 2, "Total Ingresos (S/.)", headerStyle);

            int rowNum = 1;
            double totalIngresos = 0.0;
            for (TopPlanes dato : datos) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                crearCelda(row, 0, dato.planNombre(), dataStyle);
                crearCelda(row, 1, String.valueOf(dato.cantidad()), dataStyle);
                crearCelda(row, 2, String.format("%.2f", dato.totalIngresos()), dataStyle);
                totalIngresos += dato.totalIngresos();
            }

            org.apache.poi.ss.usermodel.Row totalRow = sheet.createRow(rowNum);
            crearCelda(totalRow, 0, "TOTAL GENERAL", headerStyle);
            crearCelda(totalRow, 1, "", headerStyle);
            crearCelda(totalRow, 2, String.format("%.2f", totalIngresos), headerStyle);

            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generando Excel de top planes", e);
            throw new RuntimeException("Error generando Excel", e);
        }
    }

    // ========================================
    // UTILIDADES PDF
    // ========================================

    private void agregarCeldaHeader(PdfPTable table, String texto) {
        com.lowagie.text.Font font = new com.lowagie.text.Font(
                com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD, java.awt.Color.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(new java.awt.Color(255, 215, 0));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void agregarCelda(PdfPTable table, String texto) {
        com.lowagie.text.Font font = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9);
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    // ========================================
    // UTILIDADES EXCEL
    // ========================================

    private CellStyle crearEstiloHeader(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GOLD.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloData(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void crearCelda(org.apache.poi.ss.usermodel.Row row, int column, String value, CellStyle style) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void crearHojaResumen(Sheet sheet, ResumenIngresoDTO resumen, Workbook workbook) {
        CellStyle headerStyle = crearEstiloHeader(workbook);
        CellStyle dataStyle = crearEstiloData(workbook);

        int rowNum = 0;
        org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(rowNum++);
        crearCelda(titleRow, 0, "RESUMEN DE INGRESOS", headerStyle);
        rowNum++; // Espacio

        org.apache.poi.ss.usermodel.Row row1 = sheet.createRow(rowNum++);
        crearCelda(row1, 0, "Total Confirmado", headerStyle);
        crearCelda(row1, 1, "S/. " + String.format("%.2f", resumen.getTotalConfirmado()), dataStyle);

        org.apache.poi.ss.usermodel.Row row2 = sheet.createRow(rowNum++);
        crearCelda(row2, 0, "Total Pendiente", headerStyle);
        crearCelda(row2, 1, "S/. " + String.format("%.2f", resumen.getTotalPendiente()), dataStyle);

        org.apache.poi.ss.usermodel.Row row3 = sheet.createRow(rowNum++);
        crearCelda(row3, 0, "Total Cancelado", headerStyle);
        crearCelda(row3, 1, "S/. " + String.format("%.2f", resumen.getTotalCancelado()), dataStyle);

        org.apache.poi.ss.usermodel.Row row4 = sheet.createRow(rowNum++);
        crearCelda(row4, 0, "TOTAL GENERAL", headerStyle);
        crearCelda(row4, 1, "S/. " + String.format("%.2f", resumen.getTotalGeneral()), headerStyle);

        rowNum++; // Espacio
        org.apache.poi.ss.usermodel.Row row5 = sheet.createRow(rowNum++);
        crearCelda(row5, 0, "Transacciones Confirmadas", headerStyle);
        crearCelda(row5, 1, String.valueOf(resumen.getCantidadConfirmadas()), dataStyle);

        org.apache.poi.ss.usermodel.Row row6 = sheet.createRow(rowNum++);
        crearCelda(row6, 0, "Transacciones Pendientes", headerStyle);
        crearCelda(row6, 1, String.valueOf(resumen.getCantidadPendientes()), dataStyle);

        org.apache.poi.ss.usermodel.Row row7 = sheet.createRow(rowNum++);
        crearCelda(row7, 0, "Transacciones Canceladas", headerStyle);
        crearCelda(row7, 1, String.valueOf(resumen.getCantidadCanceladas()), dataStyle);

        org.apache.poi.ss.usermodel.Row row8 = sheet.createRow(rowNum++);
        crearCelda(row8, 0, "TOTAL TRANSACCIONES", headerStyle);
        crearCelda(row8, 1, String.valueOf(resumen.getCantidadTransacciones()), headerStyle);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void crearHojaDetalleIngresos(Sheet sheet, List<IngresoDetalladoDTO> ingresos, Workbook workbook) {
        CellStyle headerStyle = crearEstiloHeader(workbook);
        CellStyle dataStyle = crearEstiloData(workbook);

        org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
        crearCelda(headerRow, 0, "Fecha", headerStyle);
        crearCelda(headerRow, 1, "DNI", headerStyle);
        crearCelda(headerRow, 2, "Usuario", headerStyle);
        crearCelda(headerRow, 3, "Plan", headerStyle);
        crearCelda(headerRow, 4, "Monto (S/.)", headerStyle);
        crearCelda(headerRow, 5, "Método", headerStyle);
        crearCelda(headerRow, 6, "Estado", headerStyle);
        crearCelda(headerRow, 7, "Código", headerStyle);

        int rowNum = 1;
        for (IngresoDetalladoDTO ingreso : ingresos) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
            crearCelda(row, 0, ingreso.getFechaPago().format(DATE_FORMATTER), dataStyle);
            crearCelda(row, 1, ingreso.getUsuarioDni(), dataStyle);
            crearCelda(row, 2, ingreso.getUsuarioNombre(), dataStyle);
            crearCelda(row, 3, ingreso.getPlanNombre(), dataStyle);
            crearCelda(row, 4, String.format("%.2f", ingreso.getMonto()), dataStyle);
            crearCelda(row, 5, ingreso.getMetodoPago(), dataStyle);
            crearCelda(row, 6, ingreso.getEstado(), dataStyle);
            crearCelda(row, 7, ingreso.getCodigoPago(), dataStyle);
        }

        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
