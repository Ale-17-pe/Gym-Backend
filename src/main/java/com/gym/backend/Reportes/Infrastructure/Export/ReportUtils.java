package com.gym.backend.Reportes.Infrastructure.Export;

import com.lowagie.text.*;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.core.io.ClassPathResource;

import java.awt.*;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
public class ReportUtils {

    // ===============================
    // Cargar Logo
    // ===============================
    public static Image loadLogo() {
        try {
            ClassPathResource res = new ClassPathResource("logo.png");
            InputStream is = res.getInputStream();
            Image img = Image.getInstance(is.readAllBytes());
            img.scaleToFit(95, 95);
            img.setAlignment(Image.ALIGN_LEFT);
            return img;
        } catch (Exception e) {
            log.error("Error cargando logo", e);
            return null;
        }
    }

    // ===============================
    // Pie de Página PDF
    // ===============================
    public static void addFooter(Document doc, PdfWriter writer) {
        PdfContentByte cb = writer.getDirectContent();
        cb.beginText();
        try {
            cb.setFontAndSize(BaseFont.createFont(), 9);
        } catch (Exception ignored) {}

        cb.showTextAligned(
                Element.ALIGN_CENTER,
                "Ares Fitness © | Generado: " + LocalDateTime.now(),
                300,
                20,
                0
        );
        cb.endText();
    }

    // ===============================
    // Crear Chart Básico
    // ===============================
    public static JFreeChart createBarChart(String title, Map<String, Number> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        data.forEach((k, v) -> dataset.addValue(v, "Valor", k));

        return ChartFactory.createBarChart(
                title,
                "Categoría",
                "Valor",
                dataset
        );
    }
}
