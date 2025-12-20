package com.gym.backend.Reportes.Infrastructure.Export;

import com.gym.backend.Reportes.Domain.Record.UsuariosNuevos;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class ReporteUsuariosNuevosPDF {

    public byte[] generar(List<UsuariosNuevos> data) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 36, 36, 70, 50);
        PdfWriter writer = PdfWriter.getInstance(doc, out);
        doc.open();

        Image logo = ReportUtils.loadLogo();
        if (logo != null)
            doc.add(logo);

        doc.add(new Paragraph(
                "REPORTE: USUARIOS NUEVOS POR MES\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell("Mes");
        table.addCell("Año");
        table.addCell("Cantidad");

        LinkedHashMap<String, Number> chartData = new LinkedHashMap<>();

        for (UsuariosNuevos r : data) {
            String mesAnio = r.mes() + "/" + r.anio();
            table.addCell(String.valueOf(r.mes()));
            table.addCell(String.valueOf(r.anio()));
            table.addCell(r.cantidad().toString());
            chartData.put(mesAnio, r.cantidad());
        }

        doc.add(table);
        doc.add(new Paragraph("\n"));

        JFreeChart chart = ReportUtils.createBarChart("Usuarios Nuevos", chartData);
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(chartOut, chart, 600, 300);

        Image chartImg = Image.getInstance(chartOut.toByteArray());
        chartImg.scaleToFit(500, 300);
        doc.add(chartImg);

        ReportUtils.addFooter(doc, writer);
        doc.close();
        return out.toByteArray();
    }
}
