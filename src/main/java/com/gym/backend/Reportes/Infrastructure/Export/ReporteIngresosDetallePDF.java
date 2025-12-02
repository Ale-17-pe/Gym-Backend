package com.gym.backend.Reportes.Infrastructure.Export;

import com.gym.backend.Reportes.Domain.DTO.ResumenIngresoDTO;
import com.gym.backend.Reportes.Domain.Record.IngresoDetallado;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteIngresosDetallePDF {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(0, 51, 102);
    private static final DeviceRgb TOTAL_COLOR = new DeviceRgb(255, 213, 0);

    public byte[] generar(List<IngresoDetallado> data, ResumenIngresoDTO resumen) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Título
        Paragraph title = new Paragraph("REPORTE DETALLADO DE INGRESOS")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        // Tabla de datos
        float[] columnWidths = { 100, 150, 80, 120, 80, 80, 80, 100 };
        Table table = new Table(UnitValue.createPointArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Headers
        String[] headers = { "Fecha", "Usuario", "DNI", "Plan", "Monto", "Método", "Estado", "Código" };
        for (String header : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(HEADER_COLOR)
                    .setTextAlignment(TextAlignment.CENTER));
        }

        // Data rows
        for (IngresoDetallado ingreso : data) {
            table.addCell(new Cell().add(new Paragraph(ingreso.fechaPago().format(formatter)).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(ingreso.usuarioNombre()).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(ingreso.usuarioDni()).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(ingreso.planNombre()).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(String.format("S/. %.2f", ingreso.monto())).setFontSize(9))
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new Cell().add(new Paragraph(ingreso.metodoPago()).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(ingreso.estado()).setFontSize(9))
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(ingreso.codigoPago()).setFontSize(8)));
        }

        document.add(table);

        // Resumen
        document.add(new Paragraph("\nRESUMEN").setBold().setFontSize(14).setMarginTop(20));

        Table resumenTable = new Table(2);
        resumenTable.setWidth(UnitValue.createPercentValue(50));
        resumenTable.setMarginTop(10);

        addResumenRow(resumenTable, "Total Confirmado:", resumen.getTotalConfirmado(), false);
        addResumenRow(resumenTable, "Total Pendiente:", resumen.getTotalPendiente(), false);
        addResumenRow(resumenTable, "Total Cancelado:", resumen.getTotalCancelado(), false);
        addResumenRow(resumenTable, "TOTAL GENERAL:", resumen.getTotalGeneral(), true);

        document.add(resumenTable);

        // Transacciones
        Table transTable = new Table(2);
        transTable.setWidth(UnitValue.createPercentValue(50));
        transTable.setMarginTop(10);

        transTable.addCell(new Cell().add(new Paragraph("Transacciones Totales:").setBold()));
        transTable.addCell(new Cell().add(new Paragraph(String.valueOf(resumen.getCantidadTransacciones()))));
        transTable.addCell(new Cell().add(new Paragraph("Confirmadas:").setBold()));
        transTable.addCell(new Cell().add(new Paragraph(String.valueOf(resumen.getCantidadConfirmadas()))));
        transTable.addCell(new Cell().add(new Paragraph("Pendientes:").setBold()));
        transTable.addCell(new Cell().add(new Paragraph(String.valueOf(resumen.getCantidadPendientes()))));

        document.add(transTable);

        document.close();
        return out.toByteArray();
    }

    private void addResumenRow(Table table, String label, Double value, boolean isTotal) {
        Cell labelCell = new Cell().add(new Paragraph(label).setBold());
        Cell valueCell = new Cell().add(new Paragraph(String.format("S/. %.2f", value)))
                .setTextAlignment(TextAlignment.RIGHT);

        if (isTotal) {
            labelCell.setBackgroundColor(TOTAL_COLOR);
            valueCell.setBackgroundColor(TOTAL_COLOR);
        }

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}
