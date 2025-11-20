package com.gym.backend.Reportes.Infrastructure.Export;

import com.gym.backend.Reportes.Domain.Record.IngresosMensuales;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.jfree.chart.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class ReporteIngresosPDF {

    public byte[] generar(List<IngresosMensuales> data) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 36, 36, 70, 50);
        PdfWriter writer = PdfWriter.getInstance(doc, out);
        doc.open();

        // Header con logo
        Image logo = ReportUtils.loadLogo();
        if (logo != null) doc.add(logo);

        doc.add(new Paragraph(
                "REPORTE DE INGRESOS MENSUALES\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)
        ));

        // Tabla
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell("Mes");
        table.addCell("Monto");

        LinkedHashMap<String, Number> chartData = new LinkedHashMap<>();

        for (IngresosMensuales r : data) {
            table.addCell(r.mes());
            table.addCell(String.valueOf(r.total()));
            chartData.put(r.mes(), r.total());
        }

        doc.add(table);
        doc.add(new Paragraph("\n"));

        // Gráfico
        JFreeChart chart = ReportUtils.createBarChart("Ingresos Mensuales", chartData);
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(chartOut, chart, 600, 300);
        Image chartImg = Image.getInstance(chartOut.toByteArray());
        chartImg.scaleToFit(500, 300);
        doc.add(chartImg);

        // Pie de Página
        ReportUtils.addFooter(doc, writer);

        doc.close();
        return out.toByteArray();
    }
}
