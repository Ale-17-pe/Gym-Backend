package com.gym.backend.Reportes.Infrastructure.Export;

import com.gym.backend.Reportes.Domain.Record.TopPlanes;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Component
public class ReporteTopPlanesPDF {

    public byte[] generar(List<TopPlanes> data) throws Exception {

        Document document = new Document(PageSize.A4, 40, 40, 80, 60);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, out);

        document.open();

        Image logo = ReportUtils.loadLogo();
        if (logo != null) document.add(logo);

        document.add(new Paragraph(
                "REPORTE: TOP PLANES MÁS VENDIDOS\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)
        ));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell("Plan");
        table.addCell("Cantidad");

        LinkedHashMap<String, Number> chartData = new LinkedHashMap<>();

        for (TopPlanes r : data) {
            table.addCell(r.nombrePlan());
            table.addCell(r.cantidad().toString());
            chartData.put(r.nombrePlan(), r.cantidad());
        }

        document.add(table);
        document.add(new Paragraph("\n"));

        JFreeChart chart = ReportUtils.createBarChart("Top Planes", chartData);
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(chartOut, chart, 600, 300);

        Image chartImg = Image.getInstance(chartOut.toByteArray());
        chartImg.scaleToFit(500, 300);
        document.add(chartImg);

        ReportUtils.addFooter(document, writer);
        document.close();
        return out.toByteArray();
    }
}
