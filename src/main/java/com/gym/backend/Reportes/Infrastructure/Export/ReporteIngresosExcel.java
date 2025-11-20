package com.gym.backend.Reportes.Infrastructure.Export;

import com.gym.backend.Reportes.Domain.Record.IngresosMensuales;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
public class ReporteIngresosExcel {

    public byte[] generar(List<IngresosMensuales> data) throws Exception {

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Ingresos");

        // ================== INSERTAR LOGO ==================
        InputStream is = new ClassPathResource("logo.png").getInputStream();
        byte[] bytes = is.readAllBytes();
        int picIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);

        CreationHelper helper = wb.getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setCol1(0);
        anchor.setRow1(0);
        Picture pict = drawing.createPicture(anchor, picIdx);
        pict.resize(2.5);

        // ================== HEADER ==================
        Row header = sheet.createRow(5);

        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);

        header.createCell(0).setCellValue("Mes");
        header.createCell(1).setCellValue("Total");
        header.getCell(0).setCellStyle(style);
        header.getCell(1).setCellStyle(style);

        // ================== CONTENIDO ==================
        int rowIdx = 6;

        for (IngresosMensuales r : data) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(r.mes());
            row.createCell(1).setCellValue(r.total());
        }

        // Autosize columnas
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        return out.toByteArray();
    }
}
