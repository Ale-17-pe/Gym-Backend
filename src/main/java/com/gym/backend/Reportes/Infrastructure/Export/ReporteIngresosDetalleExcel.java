package com.gym.backend.Reportes.Infrastructure.Export;

import com.gym.backend.Reportes.Domain.DTO.ResumenIngresoDTO;
import com.gym.backend.Reportes.Domain.Record.IngresoDetallado;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteIngresosDetalleExcel {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generar(List<IngresoDetallado> data, ResumenIngresoDTO resumen) throws Exception {

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Ingresos Detallados");

        // Estilos
        CellStyle headerStyle = createHeaderStyle(wb);
        CellStyle dataStyle = createDataStyle(wb);
        CellStyle moneyStyle = createMoneyStyle(wb);
        CellStyle totalStyle = createTotalStyle(wb);

        // Logo
        try {
            InputStream is = new ClassPathResource("logo.png").getInputStream();
            int picId = wb.addPicture(is.readAllBytes(), Workbook.PICTURE_TYPE_PNG);
            Drawing<?> draw = sheet.createDrawingPatriarch();
            CreationHelper helper = wb.getCreationHelper();
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(0);
            anchor.setRow1(0);
            draw.createPicture(anchor, picId).resize(2.5);
        } catch (Exception e) {
            // Logo opcional
        }

        // Título
        Row titleRow = sheet.createRow(5);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("REPORTE DETALLADO DE INGRESOS");
        CellStyle titleStyle = wb.createCellStyle();
        Font titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);

        // Headers
        Row header = sheet.createRow(7);
        String[] headers = { "Fecha", "Usuario", "DNI", "Plan", "Monto", "Método", "Estado", "Código" };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data
        int rowNum = 8;
        for (IngresoDetallado ingreso : data) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(ingreso.fechaPago().format(formatter));
            row.createCell(1).setCellValue(ingreso.usuarioNombre());
            row.createCell(2).setCellValue(ingreso.usuarioDni());
            row.createCell(3).setCellValue(ingreso.planNombre());

            Cell montoCell = row.createCell(4);
            montoCell.setCellValue(ingreso.monto());
            montoCell.setCellStyle(moneyStyle);

            row.createCell(5).setCellValue(ingreso.metodoPago());
            row.createCell(6).setCellValue(ingreso.estado());
            row.createCell(7).setCellValue(ingreso.codigoPago());

            // Aplicar estilo a todas las celdas de datos
            for (int i = 0; i < 8; i++) {
                if (i != 4) { // Ya aplicamos estilo a monto
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }
        }

        // Resumen
        rowNum += 2;
        Row resumenTitleRow = sheet.createRow(rowNum++);
        Cell resumenCell = resumenTitleRow.createCell(0);
        resumenCell.setCellValue("RESUMEN");
        resumenCell.setCellStyle(titleStyle);

        // Totales
        createRow(sheet, rowNum++, "Total Confirmado:", resumen.getTotalConfirmado(), totalStyle, moneyStyle);
        createRow(sheet, rowNum++, "Total Pendiente:", resumen.getTotalPendiente(), totalStyle, moneyStyle);
        createRow(sheet, rowNum++, "Total Cancelado:", resumen.getTotalCancelado(), totalStyle, moneyStyle);

        rowNum++;
        createRow(sheet, rowNum++, "TOTAL GENERAL:", resumen.getTotalGeneral(), totalStyle, moneyStyle);

        rowNum++;
        createRowInt(sheet, rowNum++, "Transacciones Totales:", resumen.getCantidadTransacciones(), totalStyle,
                dataStyle);

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        return out.toByteArray();
    }

    private void createRow(XSSFSheet sheet, int rowNum, String label, Double value, CellStyle labelStyle,
            CellStyle valueStyle) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(3);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);

        Cell valueCell = row.createCell(4);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(valueStyle);
    }

    private void createRowInt(XSSFSheet sheet, int rowNum, String label, Integer value, CellStyle labelStyle,
            CellStyle valueStyle) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(3);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);

        Cell valueCell = row.createCell(4);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(valueStyle);
    }

    // Estilos
    private CellStyle createHeaderStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private CellStyle createMoneyStyle(XSSFWorkbook wb) {
        CellStyle style = createDataStyle(wb);
        DataFormat format = wb.createDataFormat();
        style.setDataFormat(format.getFormat("S/. #,##0.00"));
        return style;
    }

    private CellStyle createTotalStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
}
