package com.gym.backend.Reportes.Infrastructure.Export;

import com.gym.backend.Reportes.Domain.Record.TopPlanes;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
public class ReporteTopPlanesExcel {

    public byte[] generar(List<TopPlanes> data) throws Exception {

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Top Planes");

        InputStream is = new ClassPathResource("logo.png").getInputStream();
        int picId = wb.addPicture(is.readAllBytes(), Workbook.PICTURE_TYPE_PNG);
        Drawing<?> draw = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setCol1(0);
        anchor.setRow1(0);
        draw.createPicture(anchor, picId).resize(2.5);

        Row header = sheet.createRow(5);
        header.createCell(0).setCellValue("Plan");
        header.createCell(1).setCellValue("Cantidad");

        int row = 6;
        for (TopPlanes r : data) {
            Row rw = sheet.createRow(row++);
            rw.createCell(0).setCellValue(r.nombrePlan());
            rw.createCell(1).setCellValue(r.cantidad());
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        return out.toByteArray();
    }
}
