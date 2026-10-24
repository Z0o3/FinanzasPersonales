package com.example.finanzas.report;

import com.example.finanzas.model.Movimiento;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfReportStrategy implements ReportStrategy {
    static final String[] ENCABEZADOS = {"Fecha", "Tipo", "Monto", "Concepto", "Categoría", "Cuenta", "Estado", "Método", "Etiquetas"};

    @Override
    public String extension() { return ".pdf"; }

    @Override
    public void generar(List<Movimiento> datos, File destino, String titulo) throws Exception {
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(destino));
             Document doc = new Document(pdf, PageSize.A4.rotate())) {
            // En iText 9 ya no existe setBold(): se usa una fuente en negritas
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            doc.add(new Paragraph(titulo).setFont(bold).setFontSize(16));
            doc.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                    .setFontSize(8));

            Table t = new Table(UnitValue.createPercentArray(new float[]{1.2f, 1f, 1.4f, 3f, 1.8f, 2f, 1.4f, 1.8f, 2f}))
                    .useAllAvailableWidth();
            for (String h : ENCABEZADOS) {
                t.addHeaderCell(new Cell().add(new Paragraph(h).setFont(bold).setFontSize(9))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }
            double ing = 0, gas = 0;
            for (Movimiento m : datos) {
                if ("INGRESO".equals(m.getTipo())) ing += m.getMonto(); else gas += m.getMonto();
                String[] fila = {m.getFecha().toString(), m.getTipo(), String.format("$%,.2f", m.getMonto()), m.getConcepto(),
                        m.getCategoria(), m.getCuenta(), m.getEstado(), m.getMetodoPago(), m.getEtiquetas()};
                for (String s : fila) {
                    t.addCell(new Cell().add(new Paragraph(s == null ? "" : s).setFontSize(8)));
                }
            }
            doc.add(t);
            doc.add(new Paragraph(String.format("Movimientos: %d   |   Ingresos: $%,.2f   |   Gastos: $%,.2f   |   Balance: $%,.2f",
                    datos.size(), ing, gas, ing - gas)).setFont(bold).setFontSize(9).setMarginTop(10));
        }
    }
}